package dev.mixi.raichou

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import dev.mixi.raichou.databinding.ActivityMediumPostBinding
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

class MediumPostActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMediumPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_medium_post)
        binding.list.setHasFixedSize(true)
        setSupportActionBar(binding.toolbar)

        showListWithPermissionCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.medium_post_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.post -> {
                post()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQ_CODE_STORAGE_PERMISSION
            )
        } else {
            showList()
        }
    }

    private fun showList() {
        contentResolver.query(
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
        } ?: Toast.makeText(this, "Failed to get cursor", Toast.LENGTH_LONG).show()
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

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}
