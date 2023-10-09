package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.data_transfer_object.Post
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard

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
//            onLikeListener = { post: Post -> postViewModel.updateLikesById(post.id) },
//            onShareListener = { post: Post -> postViewModel.updateSharesById(post.id) },
//            onRemoveListener = { post: Post -> postViewModel.removeById(post.id) }
            object : OnInteractionListener {
                override fun onLike(post: Post) =
                    postViewModel.updateLikesById(post.id)

                override fun onShare(post: Post) =
                    postViewModel.updateSharesById(post.id)

                override fun onEdit(post: Post) =
                    postViewModel.setToEdit(post)


                override fun onRemove(post: Post) =
                    postViewModel.removeById(post.id)
            }
        )

        binding.apply {
            postList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                binding.postList.adapter = postAdapter
            }

            addButton.setOnClickListener {
                val text = binding.contentEditText.text.toString()
                if (text.isBlank()) {
                    Toast.makeText(
                        this@MainActivity, R.string.error_empty_content, Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                postViewModel.changeContent(text)
                postViewModel.savePost()

                hideEditTab(binding)
                clearEditView(it)
//                AndroidUtils.hideKeyboard(it) // TODO: find out why keyboard doesn't hide after
//                                              // clearEditView method
            }

            cancelEditButton.setOnClickListener {
                hideEditTab(binding)
                clearEditView(it)
            }
        }

        postViewModel.apply {
            postData.observe(this@MainActivity) { postList ->
                postAdapter.submitList(postList)
            }

            currentPost.observe(this@MainActivity) { post ->
                if (post.id != 0L) {
                    binding.apply {
                        contentEditText.setText(post.content)
                        contentEditText.focusAndShowKeyboard()

                        editPostText.text = post.content
                        editGroup.visibility = View.VISIBLE
                    }
                }

            }
        }
    }

    private fun clearEditView(view: View) {
        binding.contentEditText.setText("")
        binding.contentEditText.clearFocus()
        AndroidUtils.hideKeyboard(view)
    }

    private fun hideEditTab(binding: ActivityMainBinding) {
        binding.apply {
            editGroup.visibility = View.GONE
            editPostText.text = ""
        }
    }
}