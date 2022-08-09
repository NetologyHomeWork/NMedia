package ru.netology.nmedia.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val content = "content"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let {
                try {
                    when(Action.valueOf(it)) {
                    Action.LIKE -> {
                        handleLike(
                            gson.fromJson(message.data[content], Like::class.java))
                    }
                        Action.POST -> {
                            handlePost(
                                gson.fromJson(message.data[content], NewPostNotification::class.java))
                        }
                }
            } catch (e: RuntimeException) {
                handleUpdate()
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.e(NEW_TOKEN_TAG, token)
    }

    private fun handleLike(content: Like) {
        val myBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(myBitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(myBitmap).bigLargeIcon(null))
            .setContentTitle(getString(
                R.string.notification_user_liked,
                content.userName,
                content.postAuthor
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handlePost(content: NewPostNotification) {
        val myBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_large)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(myBitmap)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.postContent))
            .setContentTitle(getString(
                R.string.notification_user_posted,
                content.postAuthor
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleUpdate() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_update))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private companion object {
        private const val NEW_TOKEN_TAG = "NEW_TOKEN_TAG"
        private const val CHANNEL_ID = "777"
    }
}