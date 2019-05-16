package dev.mixi.raichou

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.squareup.picasso.Picasso
import dev.mixi.raichou.databinding.ItemMediumBinding

private val ImageModelDiff = object : DiffUtil.ItemCallback<ImageItemModel>() {
    override fun areItemsTheSame(oldItem: ImageItemModel, newItem: ImageItemModel) = oldItem.uri == newItem.uri
    override fun areContentsTheSame(oldItem: ImageItemModel, newItem: ImageItemModel) = oldItem == newItem
}

class ImageListAdapter(
    private val selectable: Boolean = true
) : ListAdapter<ImageItemModel, ImageItemHolder>(ImageModelDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemHolder {
        val binding = DataBindingUtil.inflate<ItemMediumBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_medium,
            parent,
            false
        )
        binding.selectable = selectable
        return ImageItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageItemHolder, position: Int) {
        val item = getItem(position)
        Picasso.get()
            .load(item.uri)
            .fit()
            .centerCrop()
            .into(holder.binding.image)
        holder.binding.selected = item.selected
    }

    fun getSelectedImageList() = currentList.filter { model -> model.selected.get() }
}