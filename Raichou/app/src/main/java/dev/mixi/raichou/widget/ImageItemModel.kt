package dev.mixi.raichou.widget

import android.net.Uri
import androidx.databinding.ObservableBoolean


data class ImageItemModel(
    val uri: Uri
) {
    val selected: ObservableBoolean = ObservableBoolean(false)
}