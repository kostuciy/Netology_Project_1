package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var testPost = Post(
            0,
            "Нетология",
            "20 сентября 8:52",
            "Текст текст текст",
            999,
            10999,
            1_300_000,
            false
        )

        updatePostView(binding, testPost)

        binding.apply {
            like.setOnClickListener {
                testPost = updateLikes(testPost)
                updatePostView(binding, testPost)
            }

            share.setOnClickListener {
                testPost = updateShares(testPost)
                updatePostView(binding, testPost)
            }

//            for tests
            root.setOnClickListener {
                println("tada")
            }
        }
    }

    private fun updateLikes(post: Post): Post {
        val newLikeAmount =
            post.likes + if (!post.likedByMe) 1 else -1

        return post.copy(
            likes = newLikeAmount,
            likedByMe = !post.likedByMe
        )
    }

    private fun updateShares(post: Post) =
        post.copy(
            shares = post.shares + 1
        )

    private fun updatePostView(binding: ActivityMainBinding, post: Post) {
        binding.apply {
            author.text = post.author
            date.text = post.publishedDate
            postText.text = post.content
            likeText.text = formatPostNumbers(post.likes)
            shareText.text = formatPostNumbers(post.shares)
            viewsText.text = formatPostNumbers(post.views)
            like.setImageResource(
                if (post.likedByMe) R.drawable.baseline_favorite_border_red_24
                else R.drawable.baseline_favorite_border_24
            )
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