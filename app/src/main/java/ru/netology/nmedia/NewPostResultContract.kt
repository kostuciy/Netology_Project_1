package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewPostResultContract : ActivityResultContract<Pair<String?, String?>, Pair<String?, String?>?>() {

    override fun createIntent(context: Context, input: Pair<String?, String?>): Intent {
        val intent = Intent(context, PostActivity::class.java)
        if (!input.first.isNullOrBlank()) intent.putExtra(EXTRA_CONTENT_TEXT, input.first)
        if (!input.second.isNullOrBlank()) intent.putExtra(EXTRA_VIDEO_LINK_TEXT, input.second)

        return intent
    }
    
    override fun parseResult(resultCode: Int, intent: Intent?): Pair<String?, String?>? =
        if (resultCode == Activity.RESULT_OK)
            intent?.getStringExtra(EXTRA_CONTENT_TEXT) to
                    intent?.getStringExtra(EXTRA_VIDEO_LINK_TEXT)
        else null
}