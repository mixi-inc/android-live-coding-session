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
import dev.mixi.raichou.databinding.ActivityMediumPostBinding
import dev.mixi.raichou.databinding.ItemMediumBinding

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
        binding.list.adapter = MyAdapter(emptyList())
        binding.list.layoutManager = GridLayoutManager(this, 3)
        binding.list.setHasFixedSize(true)

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}

class MyHolder(val binding: ItemMediumBinding) : RecyclerView.ViewHolder(binding.root) {

}

class MyAdapter(private val uris: List<Uri>) : RecyclerView.Adapter<MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = DataBindingUtil.inflate<ItemMediumBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_medium,
            parent,
            false
        )
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return uris.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        //holder.binding.image
    }

}