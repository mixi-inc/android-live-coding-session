package dev.mixi.raichou

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import dev.mixi.raichou.databinding.FragmentMediumPostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 */
class MediumPostFragment : Fragment(), CoroutineScope by MainScope() {

    lateinit var binding: FragmentMediumPostBinding

    companion object {
        private const val REQ_CODE_STORAGE_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMediumPostBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            inflateMenu(R.menu.medium_post_activity)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.post -> {
                        post()
                        true
                    }
                    else -> false
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showListWithPermissionCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun post() {
        val adapter = binding.list.adapter as ImageListAdapter
        val list = adapter.getSelectedImageList()
        val imageRef = FirebaseStorage.getInstance().reference.child("images")
        val db = FirebaseFirestore.getInstance()

        list.forEach { image ->
            val ref = imageRef.child("${image.uri.lastPathSegment}-${UUID.randomUUID()}")
            launch(Dispatchers.IO) {
                try {
                    ref.putFile(image.uri).await()
                    val downloadUri = ref.downloadUrl.await()
                    val documentRef = db.collection("images").add(
                        hashMapOf(
                            "name" to ref.name,
                            "url" to downloadUri.toString()
                        )
                    ).await()

                    Snackbar.make(
                        binding.root,
                        "Successfully added data: ${documentRef.id}",
                        Snackbar.LENGTH_SHORT
                    ).show()

                } catch (e: FirebaseFirestoreException) {
                    Timber.d("Failed to add data: $e")
                }
            }
        }
    }

    private fun showListWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQ_CODE_STORAGE_PERMISSION
            )
        } else {
            showList()
        }
    }

    private fun showList() {
        requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )?.use { cursor ->
            // if the content provider returns null in an unexpected situation
            // https://stackoverflow.com/questions/13080540/what-causes-androids-contentresolver-query-to-return-null
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val list = arrayListOf<ImageItemModel>() // ArrayList<Uri>()
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(index)
                list.add(ImageItemModel(Uri.fromFile(File(filePath))))
            }
            binding.list.adapter = ImageListAdapter(selectable = true).apply {
                submitList(list)
            }
        } ?: Toast.makeText(requireContext(), "Failed to get cursor", Toast.LENGTH_LONG).show()
    }

}
