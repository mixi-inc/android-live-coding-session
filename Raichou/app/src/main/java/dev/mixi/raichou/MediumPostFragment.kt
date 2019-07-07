package dev.mixi.raichou

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import dev.mixi.raichou.databinding.FragmentMediumPostBinding

/**
 * A simple [Fragment] subclass.
 */
class MediumPostFragment : Fragment() {

    lateinit var binding: FragmentMediumPostBinding
    val mediumPostViewModel by viewModels<MediumPostViewModel>()

    companion object {
        private const val REQ_CODE_STORAGE_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMediumPostBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            inflateMenu(R.menu.medium_post_activity)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.post -> {
                        post()
                        true
                    }
                    else -> false
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showListWithPermissionCheck()
    }

    private fun post() {
        val adapter = binding.list.adapter as ImageListAdapter
        val list = adapter.getSelectedImageList()

        mediumPostViewModel.post(list).observe(viewLifecycleOwner) { postState ->
            binding.progressbar.isVisible = postState.isLoading
            when (postState) {
                is MediumPostViewModel.PostState.Posted -> {
                    Snackbar.make(
                        binding.root,
                        "Successfully added data: ${postState.documentId}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> Unit
            }
        }
    }

    private fun showListWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQ_CODE_STORAGE_PERMISSION
            )
        } else {
            showList()
        }
    }

    private fun showList() {
        mediumPostViewModel.images.observe(viewLifecycleOwner) { list ->
            val imageListAdapter = ImageListAdapter(selectable = true)
            binding.list.adapter = imageListAdapter
            imageListAdapter.submitList(list)
        }
    }

}
