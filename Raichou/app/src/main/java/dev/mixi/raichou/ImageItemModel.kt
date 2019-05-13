package dev.mixi.raichou

import android.net.Uri
import androidx.databinding.ObservableBoolean


data class ImageItemModel(
    val uri: Uri
) {
    val selected: ObservableBoolean = ObservableBoolean(false)
}