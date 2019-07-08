package dev.mixi.raichou

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class HomeListViewModel : ViewModel() {
    val images = Firebase.firestore.collection("images")
        .snapshotLiveData<List<ImageItemModel>> { querySnapshot, _ ->
            querySnapshot ?: return@snapshotLiveData
            val list = querySnapshot.mapNotNull {
                ImageItemModel(
                    it.getField<String>("url")?.toUri() ?: return@mapNotNull null
                )
            }
            postValue(list)
        }
}