package ru.netology.nmedia.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.AttachmentManager
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var postAdapter: PostAdapter
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater, container, false)

//        setting up recycler view
        postAdapter = PostAdapter(
            object : OnInteractionListener {

                override fun onLike(post: Post) =
                    postViewModel.updateLikesById(post.id)

                override fun onShare(post: Post) {
                    postViewModel.updateSharesById(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, null)
                    startActivity(shareIntent)
                }

                override fun onEdit(post: Post) {
                    postViewModel.setToEdit(post)
                    findNavController().navigate(R.id.action_feedFragment_to_postFragment)
                }

                override fun onRemove(post: Post) =
                    postViewModel.removeById(post.id)

                override fun onVideoClick(post: Post) {
                    post.videoAttachment ?: return

                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(post.videoAttachment.link)
                    }
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    }
                }

                override fun onPostClick(post: Post) {
                    postViewModel.setToEdit(post)
                    findNavController().navigate(R.id.action_feedFragment_to_postFragment2)
                }
            },

            object : AttachmentManager {

                override fun updateVideoThumbnail(newThumbnail: Bitmap?) =
                    newThumbnail ?: BitmapFactory.decodeResource(
                            requireActivity().applicationContext.resources,
                        R.drawable.video_not_found_error
                    )
            }
        )

        binding.apply {
            postList.apply {
                layoutManager = LinearLayoutManager(context)
                binding.postList.adapter = postAdapter
            }

            floatingActionButton.setOnClickListener {
                postViewModel.setToNewPost()
                findNavController().navigate(R.id.action_feedFragment_to_postFragment)
            }

            retryButton.setOnClickListener {
                postViewModel.loadPosts()
            }
        }

        postViewModel.apply {
            postState.observe(viewLifecycleOwner) { state ->
                postAdapter.submitList(state.posts)
                binding.progress.isVisible = state.loading
                binding.errorGroup.isVisible = state.error
                binding.emptyText.isVisible = state.empty
            }
        }

        return binding.root
    }
}