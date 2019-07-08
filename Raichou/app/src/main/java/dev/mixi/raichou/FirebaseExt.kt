package dev.mixi.raichou

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

fun <T> CollectionReference.snapshotLiveData(snapshotListener: MutableLiveData<T>.(QuerySnapshot?, FirebaseFirestoreException?) -> Unit): LiveData<T> {
    return object : MutableLiveData<T>() {
        lateinit var listenerRegistration: ListenerRegistration
        override fun onActive() {
            super.onActive()
            listenerRegistration =
                addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    snapshotListener(querySnapshot, firebaseFirestoreException)
                }
        }

        override fun onInactive() {
            super.onInactive()
            listenerRegistration.remove()
        }
    }
}