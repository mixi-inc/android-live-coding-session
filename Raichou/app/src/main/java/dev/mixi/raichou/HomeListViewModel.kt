package dev.mixi.raichou

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect

class HomeListViewModel : ViewModel() {

    val images = liveData {
        // https://medium.com/@elizarov/callbacks-and-kotlin-flows-2b53aa2525cf
        callbackFlow {
            val listenerRegistration = FirebaseFirestore
                .getInstance()
                .collection("images")
                .addSnapshotListener { snapshot, e ->
                    when {
                        e != null ->
                            close(e)
                        snapshot == null ->
                            close()
                        else -> {
                            val list = snapshot.map {
                                ImageItemModel(it.data["url"].toString().toUri())
                            }
                            offer(list)
                        }
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }.collect { list ->
            emit(list)
        }
    }
}