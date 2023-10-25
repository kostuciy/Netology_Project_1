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
            edit.apply {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { postText ->
                    this.setText(postText)
                }
                requestFocus()
            }

            fabSend.setOnClickListener {
                val intent = Intent()
                if (binding.edit.text.isBlank())
                    setResult(RESULT_CANCELED, intent)
                else {
                    val content = binding.edit.text.toString()
                    intent.putExtra(Intent.EXTRA_TEXT, content)
                    setResult(RESULT_OK, intent)
                }
                finish()
            }
        }
    }
}