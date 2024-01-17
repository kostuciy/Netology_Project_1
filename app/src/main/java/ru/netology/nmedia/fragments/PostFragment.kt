package ru.netology.nmedia.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.AttachmentManager
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import java.util.concurrent.Executors

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(layoutInflater, container, false)

        val postInteractionListener = object : OnInteractionListener {

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
                findNavController().navigate(R.id.action_postFragment2_to_postFragment)
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
                findNavController().navigateUp()
            }

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

            override fun onPostClick(post: Post) {}
        }

        val postAttachmentManager = object : AttachmentManager {

            override fun updateVideoThumbnail(newThumbnail: Bitmap?) =
                newThumbnail ?: BitmapFactory.decodeResource(
                    requireActivity().applicationContext.resources,
                    R.drawable.video_not_found_error
                )
        }
        val post = postViewModel.currentPost.value ?:
        findNavController().navigateUp() as Post

        var likeCount = post.likes
        var liked = post.likedByMe
        var sharesCount = post.shares

        binding.apply {
            avatar.setImageResource(R.drawable.posts_avatar)
            author.text = post.author
            date.text = post.publishedDate
            postText.text = post.content
            views.text = formatPostNumbers(post.views)

            like.apply {
                isChecked = post.likedByMe
                text = formatPostNumbers(post.likes)
                setOnClickListener {
                    postInteractionListener.onLike(post)
                    liked = !liked
                    isChecked = liked
                    likeCount += if (liked) 1 else -1
                    text = formatPostNumbers(likeCount)
                }
            }
            share.apply {
                text = formatPostNumbers(post.shares)
                setOnClickListener {
                    postInteractionListener.onShare(post)
                    text = formatPostNumbers(++sharesCount)
                }
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.removeItem -> {
                                postInteractionListener.onRemove(post)
                                true
                            }

                            R.id.editItem -> {
                                postInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

//                checking if post has video attachment
            if (!post.videoAttachment?.link.isNullOrBlank()) {
//                    downloads thumbnail image in background so UI thread won't freeze
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                try {
                    executor.execute {
                        val thumbnailBitmap =
                            AndroidUtils.getYTVideoThumbnail(post.videoAttachment!!.link).let {
                                postAttachmentManager.updateVideoThumbnail(it)
                            }
                        val videoName = "???" // TODO: find way to get video name

                        handler.post {
                            binding.videoName.text = videoName //?: "???"
//
                            videoThumbnail.setImageBitmap(thumbnailBitmap)
                        }
                    }
                } catch (e: Exception) {
                    videoGroup.visibility = View.GONE
                }

                videoThumbnail.setOnClickListener {
                    postInteractionListener.onVideoClick(post)
                }
                videoGroup.visibility = View.VISIBLE
            } else {
                videoGroup.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun formatPostNumbers(number: Int): String =
        when {
            number >= 1_000_000 -> {
                val formattedNumber =
                    if (number <= 1_100_000)
                        "${number / 1_000_000}"
                    else String.format("%.1f", (number / 1000000.0))
                "${formattedNumber}M"
            }

            number >= 10_000 -> "${number / 1_000}K"

            number >= 1_000 -> {
                val formattedNumber =
                    if (number <= 1_100)
                        "${number / 1_000}"
                    else String.format("%.1f", (number / 1000.0))
                "${formattedNumber}K"
            }

            else -> number.toString()
        }
}