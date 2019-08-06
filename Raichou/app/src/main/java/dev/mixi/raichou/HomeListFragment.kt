package dev.mixi.raichou


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import dev.mixi.raichou.databinding.FragmentHomeListBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeListFragment : Fragment() {

    val viewModel by viewModels<HomeListViewModel>()
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

        binding.btnPost.setOnClickListener {
            Navigation.findNavController(it).navigate(HomeListFragmentDirections.toPost())
        }

        viewModel.images.observe(viewLifecycleOwner) { list ->
            binding.list.adapter = ImageListAdapter(selectable = false).apply {
                submitList(list)
            }
        }
    }
}
