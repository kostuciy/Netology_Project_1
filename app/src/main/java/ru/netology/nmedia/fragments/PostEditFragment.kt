package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class PostEditFragment : Fragment() {
    private lateinit var binding: FragmentPostEditBinding
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostEditBinding.inflate(layoutInflater, container, false)

        binding.apply {
            postViewModel.currentPost.value?.let { post ->
                edit.setText(post.content)

                post.videoAttachment?.let { video ->
                    editVideo.setText(video.link)
                }
            }

            fabSend.setOnClickListener {
                postViewModel.apply {
//                    saving new post
                    val content = binding.edit.text.toString()
                    changeContent(content)
                    val videoLink = binding.editVideo.text.toString()
                    changeVideoAttachment(videoLink)
                    savePost()
                }
            }
        }

        postViewModel.postCreated.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}