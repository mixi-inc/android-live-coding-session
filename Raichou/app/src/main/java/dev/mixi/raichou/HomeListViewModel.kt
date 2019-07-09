package dev.mixi.raichou

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeListViewModel : ViewModel() {

    val images = liveData {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("images").get().await()

        val list = snapshot.map {
            ImageItemModel(it.data["url"].toString().toUri())
        }
        emit(list)
    }
}