package dev.mixi.raichou

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mixi.raichou.databinding.ActivityMediumPostBinding
import dev.mixi.raichou.databinding.ItemMediumBinding

class MediumPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium_post)
        val binding = DataBindingUtil.setContentView<ActivityMediumPostBinding>(this, R.layout.activity_medium_post)
        binding.list.adapter = MyAdapter(emptyList())
        binding.list.layoutManager = GridLayoutManager(this, 3)
        binding.list.setHasFixedSize(true)
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