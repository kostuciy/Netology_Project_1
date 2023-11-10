package ru.netology.nmedia.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
//        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
//            if (result == null) {
//                postViewModel.setToNewPost()
//                return@registerForActivityResult
//            }
////            if received non-null content from post activity, then update view model
//
//            postViewModel.apply {
//                changeContent(result.first!!)
//                result.second?.let { link ->
//                    changeVideoAttachment(link)
//                }
//                savePost()
//            }
//        }

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
//                    newPostLauncher.launch(
//                        Pair(post.content, post.videoAttachment?.link ?: "")
//                    )
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

//            addButton.setOnClickListener {
//                val text = binding.contentEditText.text.toString()
//                if (text.isBlank()) {
//                    Toast.makeText(
//                        this@MainActivity, R.string.error_empty_content, Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
////                }
//
//                postViewModel.changeContent(text)
//                postViewModel.savePost()
//
//                hideEditTab(binding)
//                clearEditView(it)
////                AndroidUtils.hideKeyboard(it) // TODO: find out why keyboard doesn't hide after
////                                              // clearEditView method
//        }

//            cancelEditButton.setOnClickListener {
//                hideEditTab(binding)
//                clearEditView(it)
//                postViewModel.setToNewPost()
//            }
//
            floatingActionButton.setOnClickListener {
//                newPostLauncher.launch(null to null)
                postViewModel.setToNewPost()
                findNavController().navigate(R.id.action_feedFragment_to_postFragment)
            }


        }

        postViewModel.apply {
            postData.observe(viewLifecycleOwner) { postList ->
                postAdapter.submitList(postList)
            }

//            currentPost.observe(this@MainActivity) { post ->
//                if (post.id != 0L) {
//                    binding.apply {
//                        contentEditText.setText(post.content)
//                        contentEditText.focusAndShowKeyboard()
//
//                        editPostText.text = post.content
//                        editGroup.visibility = View.VISIBLE
//                    }
//                }
//
//            }
        }

        return binding.root
    }

//    private fun clearEditView(view: View) {
//        binding.contentEditText.setText("")
//        binding.contentEditText.clearFocus()
//        AndroidUtils.hideKeyboard(view)
//    }
//
//    private fun hideEditTab(binding: ActivityMainBinding) {
//        binding.apply {
//            editGroup.visibility = View.GONE
//            editPostText.text = ""
//        }
//    }
}