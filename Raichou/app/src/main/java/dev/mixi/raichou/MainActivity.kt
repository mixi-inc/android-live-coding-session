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
        Timber.plant(Timber.DebugTree())

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.btnPost.setOnClickListener { startActivity(MediumPostActivity.createIntent(this)) }
        setSupportActionBar(binding.toolbar)

        val db = FirebaseFirestore.getInstance()
        db.collection("images")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.forEach { doc ->
                    val name: String = doc.data.get("name") as String
                    val url: String = doc.data.get("url") as String
                    Timber.d("$name, $url")
                    Picasso.get()
                        .load(url)
                        .fit()
                        .centerInside()
                        .into(binding.image)
                }
            }
    }
}
