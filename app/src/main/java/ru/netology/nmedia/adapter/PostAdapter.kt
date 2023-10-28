package ru.netology.nmedia.adapter

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.util.AndroidUtils
import java.util.concurrent.Executors

interface OnInteractionListener {

    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onVideoClick(post: Post)
}

interface AttachmentManager {

    fun updateVideoThumbnail(newThumbnail: Bitmap?): Bitmap
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val attachmentObserver: AttachmentManager
    ) :
    ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback) {

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
        private val attachmentManager: AttachmentManager
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: Post) {
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
                        onInteractionListener.onLike(post)
                    }
                }
                share.apply {
                    text = formatPostNumbers(post.shares)
                    setOnClickListener {
                        onInteractionListener.onShare(post)
                    }
                }
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.removeItem -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }

                                R.id.editItem -> {
                                    onInteractionListener.onEdit(post)
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
                                    attachmentManager.updateVideoThumbnail(it)
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
                            onInteractionListener.onVideoClick(post)
                    }
                    videoGroup.visibility = View.VISIBLE
                }
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val cardPostBinging  = CardPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return PostViewHolder(cardPostBinging, onInteractionListener, attachmentObserver)
    }

    override fun onBindViewHolder(postViewHolder: PostViewHolder, position: Int) {
        val post = getItem(position)
        postViewHolder.bindData(post)
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}