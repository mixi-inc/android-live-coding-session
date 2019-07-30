import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.toLiveData(): LiveData<T> {
    val flow = this
    return liveData {
        flow.collect{
            emit(it)
        }
    }
}