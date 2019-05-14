package dev.mixi.raichou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import dev.mixi.raichou.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.btnPost.setOnClickListener { startActivity(MediumPostActivity.createIntent(this)) }

        val db = FirebaseFirestore.getInstance()
        db.collection("images")
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.first().data
                val name = data["name"] as String
                val url = data["url"] as String
                Picasso.get()
                    .load(url)
                    .fit()
                    .centerInside()
                    .into(binding.image)
                Timber.d("$name, $url")
            }
    }
}
