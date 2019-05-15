package dev.mixi.raichou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import dev.mixi.raichou.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.btnPost.setOnClickListener { startActivity(MediumPostActivity.createIntent(this)) }

        launch(Dispatchers.Main) {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("images").get().await()

            val list = snapshot.map {
                ImageItemModel(it.data["url"].toString().toUri())
            }
            binding.list.adapter = ImageListAdapter(selectable = false).apply {
                submitList(list)
            }
        }
    }
}
