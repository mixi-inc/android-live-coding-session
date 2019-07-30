package dev.mixi.raichou

import androidx.lifecycle.ViewModel
import toLiveData

class HomeListViewModel(firestore: Firestore = Firestore()) : ViewModel() {
    val images = firestore.images().toLiveData()
}
