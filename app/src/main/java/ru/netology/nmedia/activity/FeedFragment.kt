package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRetry(post: Post) {
                viewModel.edit(post)
                viewModel.save()
            }
        })
        binding.list.adapter = adapter
//        changes ui depending on interaction with server (is loading or error occurring)
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                binding.fab.visibility = View.GONE
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                        binding.fab.visibility = View.VISIBLE
                    }
                    .show()
            }
        }
//        changes ui depending on list of posts change in db
        viewModel.data.observe(viewLifecycleOwner) { postData ->
            adapter.submitList(postData.posts)
            binding.emptyText.isVisible = postData.empty
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { newPostsAmount ->
            if (newPostsAmount > 0) {
                binding.refreshButton.text = "Update to see $newPostsAmount new post(s)"
                binding.refreshButton.visibility = View.VISIBLE
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.refreshPosts()
            it.visibility = View.GONE
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            binding.list.scrollToPosition(0)
            binding.refreshButton.visibility = View.GONE
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
