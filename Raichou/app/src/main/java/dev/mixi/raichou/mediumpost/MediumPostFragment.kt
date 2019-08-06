package dev.mixi.raichou.mediumpost

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
import dev.mixi.raichou.ImageListAdapter
import dev.mixi.raichou.R
import dev.mixi.raichou.databinding.FragmentMediumPostBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class MediumPostFragment : Fragment() {

    lateinit var binding: FragmentMediumPostBinding
    val viewModel by viewModels<MediumPostViewModel>()

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

    fun post() {
        val adapter = binding.list.adapter as ImageListAdapter
        val list = adapter.getSelectedImageList()
        viewModel.post(list).observe(viewLifecycleOwner) { postResult ->
            binding.progress.isVisible = postResult.isLoading
            when (postResult) {
                MediumPostViewModel.PostResult.Loading -> Unit
                is MediumPostViewModel.PostResult.Posted -> {
                    Snackbar.make(
                        binding.root,
                        "Successfully added data: ${postResult.documentId}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is MediumPostViewModel.PostResult.Error ->
                    Timber.d("Failed to add data: ${postResult.e}")
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
        viewModel.images.observe(viewLifecycleOwner) { list ->
            binding.list.adapter = ImageListAdapter(selectable = true).apply {
                submitList(list)
            }
        }
    }

}
