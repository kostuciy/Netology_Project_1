package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.AppActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.data_transfer_object.Post
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val channelId = "server"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        message.data["action"]?.let {
            try {
                when (Actions.valueOf(it)) {
                    Actions.LIKE -> handleLike(
                        Gson().fromJson(message.data["content"], Like::class.java)
                    )

                    Actions.POST -> handlePost(
                        Gson().fromJson(message.data["content"], Post::class.java)
                    )
                }
            } catch (e: IllegalArgumentException) { // when no such action exists method
                handleException()                   // .valueOf() throws IllegalArgumentException
            }                                       // so the easiest thing to do is to catch it
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    private fun handleLike(like: Like) {
        val intent = Intent(applicationContext, AppActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentText(getString(R.string.notification_user_liked, like.userName, like.postAuthor))
            .setSmallIcon(R.mipmap.ic_launcher_netology)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(
            Random.nextInt(100_000),
            notification
        )
    }

    private fun handlePost(post: Post) {
        val intent = Intent(applicationContext, AppActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentText(getString(R.string.notification_post_created, post.author))
            .setSmallIcon(R.mipmap.ic_launcher_netology)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(post.content)
            )
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(
            Random.nextInt(100_000),
            notification
        )
    }

    private fun handleException() {
        println("No such action exists")
    }
}
enum class Actions {
    LIKE, POST
}

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
) {

}