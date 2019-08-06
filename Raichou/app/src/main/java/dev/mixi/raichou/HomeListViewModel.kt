package dev.mixi.raichou

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class HomeListViewModel : ViewModel() {
    val images = FirebaseFirestore.getInstance()
        .images()
        .toLiveData()
}

private fun <T> Flow<T>.toLiveData(): LiveData<T> {
    return liveData {
        this@toLiveData
            .collect {
                emit(it)
            }
    }
}
