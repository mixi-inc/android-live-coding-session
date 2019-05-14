package dev.mixi.raichou.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import dev.mixi.raichou.R
import dev.mixi.raichou.databinding.FragmentHomeBinding
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPost.setOnClickListener { Navigation.findNavController(it).navigate(R.id.to_post) }

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


