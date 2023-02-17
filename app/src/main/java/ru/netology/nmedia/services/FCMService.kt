package ru.netology.nmedia.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.domain.model.PushMessage
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth

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

        message.data[ACTION]?.let {
                try {
                    when(Action.valueOf(it)) {
                    Action.LIKE -> {
                        handleLike(
                            gson.fromJson(message.data[CONTENT], Like::class.java))
                    }
                        Action.POST -> {
                            handlePost(
                                gson.fromJson(message.data[CONTENT], NewPostNotification::class.java))
                        }
                }
            } catch (e: RuntimeException) {
                handleUpdate()
            }
        }
        val content = gson.fromJson(message.data[CONTENT], PushMessage::class.java)
        val recipientId = content.recipientId
        val userId = appAuth.state.value?.id
        when (recipientId) {
            userId, null -> handlePushMessage(content = content)
            else -> appAuth.sendPushToken()
        }
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
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

        sendNotificationWithCheckPermission(notification)
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

        sendNotificationWithCheckPermission(notification)
    }

    private fun handleUpdate() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_update))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        sendNotificationWithCheckPermission(notification)
    }

    private fun handlePushMessage(content: PushMessage) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification, content.recipientId, content.content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        sendNotificationWithCheckPermission(notification)
    }

    private fun sendNotificationWithCheckPermission(notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }

    private companion object {
        private const val CHANNEL_ID = "777"
        private const val ACTION = "action"
        private const val CONTENT = "content"
    }
}