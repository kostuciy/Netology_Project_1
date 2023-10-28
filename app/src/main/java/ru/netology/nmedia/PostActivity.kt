package ru.netology.nmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            intent.apply {
                getStringExtra(EXTRA_CONTENT_TEXT)?.let { postText ->
                    edit.setText(postText)
                }
                getStringExtra(EXTRA_VIDEO_LINK_TEXT)?.let {linkText ->
                    editVideo.setText(linkText)
                }
        }

            fabSend.setOnClickListener {
                val intent = Intent()
                if (binding.edit.text.isBlank())
                    setResult(RESULT_CANCELED, intent)
                else {
                    val content = binding.edit.text.toString()
                    intent.putExtra(EXTRA_CONTENT_TEXT, content)

                    val videoLink = binding.editVideo.text.toString()
                    intent.putExtra(EXTRA_VIDEO_LINK_TEXT, videoLink)

                    setResult(RESULT_OK, intent)
                }
                finish()
            }
        }
    }
}