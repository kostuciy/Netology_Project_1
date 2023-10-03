package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.CardPostBinding

typealias onPostListener = (post: Post) -> Unit

class PostAdapter(
    private val onLikeListener: onPostListener,
    private val onShareListener: onPostListener
    ) :
    ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback) {

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onLikeListener: onPostListener,
        private val onShareListener: onPostListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: Post) {
            binding.apply {
                avatar.setImageResource(R.drawable.posts_avatar)
                author.text = post.author
                date.text = post.publishedDate
                postText.text = post.content
                likeText.text = formatPostNumbers(post.likes)
                shareText.text = formatPostNumbers(post.shares)
                viewsText.text = formatPostNumbers(post.views)
                like.apply {
                    setImageResource(
                        if (post.likedByMe) R.drawable.baseline_favorite_border_red_24
                        else R.drawable.baseline_favorite_border_24
                    )
                    setOnClickListener {
                        onLikeListener(post)
                    }
                }
                share.setOnClickListener {
                    onShareListener(post)
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

        return PostViewHolder(cardPostBinging, onLikeListener, onShareListener)
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