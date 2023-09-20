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
            false
        )

        updatePostView(binding, testPost)

        binding.apply {
            like.setOnClickListener {
                testPost = updateLikes(testPost)
                updatePostView(binding, testPost)
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

    private fun updatePostView(binding: ActivityMainBinding, post: Post) {
        binding.apply {
            author.text = post.author
            date.text = post.publishedDate
            postText.text = post.content
            likeText.text =
                if (post.likes / 1000 > 0) {
                    val formattedNumber =
                        if (post.likes % 1000 == 0)
                            "1"
                        else String.format("%.1f", (post.likes / 1000.0))
                    "${formattedNumber}K"
                } else post.likes.toString()

            like.setImageResource(
                if (post.likedByMe) R.drawable.baseline_favorite_border_red_24
                else R.drawable.baseline_favorite_border_24
            )
        }
    }
}