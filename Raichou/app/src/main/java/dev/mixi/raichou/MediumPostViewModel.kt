package dev.mixi.raichou

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class MediumPostViewModel(application: Application) : AndroidViewModel(application) {
    val images = liveData<List<ImageItemModel>> {
        application.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )?.use { cursor ->
            // if the content provider returns null in an unexpected situation
            // https://stackoverflow.com/questions/13080540/what-causes-androids-contentresolver-query-to-return-null
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val list = arrayListOf<ImageItemModel>() // ArrayList<Uri>()
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(index)
                list.add(ImageItemModel(Uri.fromFile(File(filePath))))
            }
            emit(list)
        }
    }

    fun post(list: List<ImageItemModel>) = liveData {
        emit(PostState.Posting)
        val imageRef = FirebaseStorage.getInstance().reference.child("images")
        val db = FirebaseFirestore.getInstance()

        list.forEach { image ->
            val ref = imageRef.child("${image.uri.lastPathSegment}-${UUID.randomUUID()}")
            ref.putFile(image.uri).await()
            val downloadUri = ref.downloadUrl.await()
            val documentRef = db.collection("images").add(
                hashMapOf(
                    "name" to ref.name,
                    "url" to downloadUri.toString()
                )
            ).await()
            emit(PostState.Posted(documentRef.id))
        }
    }

    sealed class PostState() {
        object Posting : PostState()
        class Posted(val documentId: String) : PostState()

        val isLoading get() = this is Posting
    }
}