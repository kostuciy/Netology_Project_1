package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setting up recycler view
        postAdapter = PostAdapter(
            onLikeListener = { post: Post -> postViewModel.updateLikesById(post.id) },
            onShareListener = { post: Post -> postViewModel.updateSharesById(post.id) }
        )
        binding.postList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            binding.postList.adapter = postAdapter
        }

        postViewModel.postData.observe(this) { postList ->
            postAdapter.postList = postList
        }
    }
}