package dev.mixi.raichou

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class HomeListViewModel : ViewModel() {
    val images = liveData {
        val db = Firebase.firestore
        emitSource(object : LiveData<List<ImageItemModel>>() {
            lateinit var listenerRegistration: ListenerRegistration
            override fun onActive() {
                super.onActive()
                listenerRegistration = db.collection("images")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        querySnapshot?.let {
                            postValue(querySnapshot.mapNotNull {
                                ImageItemModel(
                                    it.getField<String>("url")?.toUri() ?: return@mapNotNull null
                                )
                            })
                        }
                    }
            }

            override fun onInactive() {
                super.onInactive()
                listenerRegistration.remove()
            }
        }
        )
    }
}