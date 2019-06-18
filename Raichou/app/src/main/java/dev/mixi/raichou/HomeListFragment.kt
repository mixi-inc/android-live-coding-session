package dev.mixi.raichou


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import dev.mixi.raichou.databinding.FragmentHomeListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * A simple [Fragment] subclass.
 */
class HomeListFragment : Fragment(), CoroutineScope by MainScope() {

    lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPost.setOnClickListener { Navigation.findNavController(it).navigate(R.id.to_post) }

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

    override fun onDestroyView() {
        super.onDestroyView()
        cancel()
    }
}
