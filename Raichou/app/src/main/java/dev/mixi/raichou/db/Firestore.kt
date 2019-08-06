package dev.mixi.raichou.db

import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dev.mixi.raichou.ImageItemModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

fun FirebaseFirestore.images(): Flow<List<ImageItemModel>> {
    return collection("images")
        .toFlow()
        .map { snapshot ->
            snapshot.map {
                ImageItemModel(it.data["url"].toString().toUri())
            }
        }
}

private fun CollectionReference.toFlow(): Flow<QuerySnapshot> {
    return callbackFlow<QuerySnapshot> {
        val listenerRegistration = addSnapshotListener { snapshot, exception ->
            if (exception != null) close(exception)
            else if (snapshot != null) {
                offer(snapshot)
            }
        }
        awaitClose { listenerRegistration.remove() }
    }
}
