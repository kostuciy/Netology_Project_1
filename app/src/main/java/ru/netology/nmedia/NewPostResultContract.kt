package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewPostResultContract : ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        val intent = Intent(context, PostActivity::class.java)
        if (!input.isNullOrBlank())
            intent.putExtra(Intent.EXTRA_TEXT, input)

        return intent
    }
    
    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK)
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        else null
}