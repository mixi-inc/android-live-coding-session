package dev.mixi.raichou.post

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import dev.mixi.raichou.R
import dev.mixi.raichou.databinding.FragmentPostBinding
import dev.mixi.raichou.ui.ImageItemModel
import dev.mixi.raichou.ui.ImageListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import java.util.UUID

private const val REQ_CODE_STORAGE_PERMISSION = 1

class PostFragment : Fragment(), Toolbar.OnMenuItemClickListener, CoroutineScope by MainScope() {

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.list.setHasFixedSize(true)
        binding.toolbar.apply {
            inflateMenu(R.menu.post)
            setOnMenuItemClickListener(this@PostFragment)
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

    override fun onMenuItemClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.post -> {
            post()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun post() {
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

                    Timber.d("Successfully added data: ${documentRef.id}")

                } catch (e: FirebaseFirestoreException) {
                    Timber.d("Failed to add data: $e")
                }
            }
        }
    }

    private fun showListWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            showList()
        } else {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQ_CODE_STORAGE_PERMISSION)
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
            binding.list.adapter = ImageListAdapter().apply {
                submitList(list)
            }
        } ?: Toast.makeText(requireActivity(), "Failed to get cursor", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showList()
                } else {
                    Snackbar.make(binding.root, "Please give me a permission", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
