package dev.mixi.raichou

import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class Firestore {
    fun images(): Flow<List<ImageItemModel>> {
        return FirebaseFirestore
            .getInstance()
            .collection("images")
            .toFlow()
            .map { snapshot ->
                snapshot.map {
                    ImageItemModel(it.data["url"].toString().toUri())
                }
            }
    }

    private fun CollectionReference.toFlow(): Flow<QuerySnapshot> {
        val collectionRef = this
        // https://medium.com/@elizarov/callbacks-and-kotlin-flows-2b53aa2525cf
        return callbackFlow<QuerySnapshot> {
            val listenerRegistration = collectionRef.addSnapshotListener { snapshot, e ->
                when {
                    e != null ->
                        close(e)
                    snapshot == null ->
                        close()
                    else -> {
                        offer(snapshot)
                    }
                }
            }
            awaitClose { listenerRegistration.remove() }
        }
    }
}
