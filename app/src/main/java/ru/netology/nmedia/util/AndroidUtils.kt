package ru.netology.nmedia.util

//import org.apache.commons.io.IOUtils
//import org.json.JSONObject
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


object AndroidUtils {

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun View.focusAndShowKeyboard() {
        /**
         * This is to be called when the window already has focus.
         */
        fun View.showTheKeyboardNow() {
            if (isFocused) {
                post {
                    // We still post the call, just in case we are being notified of the windows focus
                    // but InputMethodManager didn't get properly setup yet.
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        requestFocus()
        if (hasWindowFocus()) {
            // No need to wait for the window to get focus.
            showTheKeyboardNow()
        } else {
            // We need to wait until the window gets focus.
            viewTreeObserver.addOnWindowFocusChangeListener(
                object : ViewTreeObserver.OnWindowFocusChangeListener {
                    override fun onWindowFocusChanged(hasFocus: Boolean) {
                        // This notification will arrive just before the InputMethodManager gets set up.
                        if (hasFocus) {
                            this@focusAndShowKeyboard.showTheKeyboardNow()
                            // It’s very important to remove this listener once we are done.
                            viewTreeObserver.removeOnWindowFocusChangeListener(this)
                        }
                    }
                })
        }
    }

    private fun loadBitmapImage(imageLink: String): Bitmap? {
        val url: URL = URL(imageLink)
        var connection: HttpURLConnection
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getYTVideoId(videoLink: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(videoLink)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "error"
        }
    }

//    fun getYTVideoName(videoLink: String?): String? { TODO: fix
//        try {
//            if (videoLink != null) {
//                val embeddedURL = URL(
//                    "http://www.youtube.com/oembed?url=${videoLink}&format=json"
//                )
//                return JSONObject(IOUtils.toString(embeddedURL)).getString("title")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }

    fun getYTVideoThumbnail(videoLink: String): Bitmap? {
        val videoId = getYTVideoId(videoLink) ?: return null
        val thumbnailUrl = "https://img.youtube.com/vi/${videoId}/0.jpg"

        return loadBitmapImage(thumbnailUrl)
    }
}