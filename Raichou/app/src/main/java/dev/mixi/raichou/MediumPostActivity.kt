package dev.mixi.raichou

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dev.mixi.raichou.databinding.ActivityMediumPostBinding
import dev.mixi.raichou.databinding.ItemMediumBinding
import java.io.File
import java.util.UUID

private const val REQ_CODE_STORAGE_PERMISSION = 1

class MediumPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediumPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medium_post)

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
            return
        }

        setSupportActionBar(binding.toolbar)
        showList()
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
            ref.putFile(image.uri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Uploaded image ${image.uri}", Toast.LENGTH_SHORT).show()
                    ref.downloadUrl
                        .addOnSuccessListener { downloadUri ->
                            val data = hashMapOf(
                                "name" to ref.name,
                                "url" to downloadUri.toString()
                            )
                            db.collection("images")
                                .add(data)
                                .addOnSuccessListener { documentRef ->
                                    Toast.makeText(
                                        this,
                                        "Successfully added data: ${documentRef.id}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to add data: $e", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to get download URL: $e", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image: $e", Toast.LENGTH_SHORT).show()
                }
        }
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

    private fun showList() {
        binding.list.layoutManager = GridLayoutManager(this, 3)
        binding.list.setHasFixedSize(true)

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
            val list = arrayListOf<ImageItemViewModel>() // ArrayList<Uri>()
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(index)
                list.add(ImageItemViewModel(Uri.fromFile(File(filePath))))
            }
            binding.list.adapter = ImageListAdapter(list)
        } ?: Toast.makeText(this, "Failed to get cursor", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}

class ImageHolder(val binding: ItemMediumBinding) : RecyclerView.ViewHolder(binding.root)

class ImageItemViewModel(val uri: Uri) {
    var selected: ObservableBoolean = ObservableBoolean(false)
}

class ImageListAdapter(private val imageItemViewModels: List<ImageItemViewModel>) :
    RecyclerView.Adapter<ImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = DataBindingUtil.inflate<ItemMediumBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_medium,
            parent,
            false
        )
        return ImageHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageItemViewModels.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Picasso.get()
            .load(imageItemViewModels[position].uri)
            .into(holder.binding.image)
        holder.binding.imageViewModel = imageItemViewModels[position]
    }

    fun getSelectedImageList(): List<ImageItemViewModel> {
        return imageItemViewModels.filter { model ->
            model.selected.get()
        }
    }
}