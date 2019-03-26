package dev.mixi.raichou

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.mixi.raichou.databinding.ActivityMediumPostBinding
import dev.mixi.raichou.databinding.ItemMediumBinding
import java.io.File

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

        showList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showList()
                } else {
                    // TODO: show dialog
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
        ).use { cursor ->
            when (cursor) {
                null -> TODO("should handle error")
                else -> {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    val list = arrayListOf<Uri>() // ArrayList<Uri>()
                    while (cursor.moveToNext()) {
                        val filePath = cursor.getString(index)
                        list.add(Uri.fromFile(File(filePath)))
                    }
                    binding.list.adapter = ImageListAdapter(list)
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}

class ImageHolder(val binding: ItemMediumBinding) : RecyclerView.ViewHolder(binding.root)

class ImageListAdapter(private val uris: List<Uri>) : RecyclerView.Adapter<ImageHolder>() {
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
        return uris.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Picasso.get()
            .load(uris[position])
            .into(holder.binding.image)
    }

}