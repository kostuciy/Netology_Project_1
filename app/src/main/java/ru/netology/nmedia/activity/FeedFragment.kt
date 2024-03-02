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
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val postViewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                postViewModel.edit(post)
            }

            override fun onLike(post: Post) {
                postViewModel.likeById(post)
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post)
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
                postViewModel.edit(post)
                postViewModel.save()
            }

            override fun onImageClick(imageUrl: String) {
                val action = FeedFragmentDirections
                        .actionFeedFragmentToImageFragment()
                        .setImageUrl(imageUrl)
                findNavController().navigate(action)
            }
        })

        binding.apply {
            list.adapter = adapter
            fab.isVisible = authViewModel.authenticated

            refreshButton.setOnClickListener {
                postViewModel.refreshPosts()
                binding.list.smoothScrollToPosition(0)
                it.visibility = View.GONE
            }
            swiperefresh.setOnRefreshListener {
                postViewModel.refreshPosts()
                binding.refreshButton.visibility = View.GONE
            }
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        postViewModel.apply {
            dataState.observe(viewLifecycleOwner) { state ->
                binding.progress.isVisible = state.loading
                binding.swiperefresh.isRefreshing = state.refreshing
                if (state.error) {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) {
                            postViewModel.loadPosts()
                            binding.fab.visibility = View.VISIBLE
                        }
                        .show()
                }
            }
            data.observe(viewLifecycleOwner) { postData ->
                val newPost =
                    adapter.currentList.size > postData.posts.filter { it.onScreen }.size
                adapter.submitList(postData.posts.filter { it.onScreen })
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }

                binding.emptyText.isVisible = postData.empty
            }
            newerCount.observe(viewLifecycleOwner) { newPostsAmount ->
                if (newPostsAmount > 0)
                    binding.refreshButton.visibility = View.VISIBLE
            }
        }


        return binding.root
    }
}
