package ru.netology.nmedia.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.netology.nmedia.data.auth.AuthService
import ru.netology.nmedia.domain.model.PushToken

@HiltWorker
class SendPushTokenWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val authService: AuthService,
    private val messaging: FirebaseMessaging
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val token = inputData.getString(TOKEN_KEY)
        try {
            val pushToken = PushToken(token ?: messaging.token.await())
            authService.sendPushToken(pushToken)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
         const val WORKER_NAME = "WORKER_NAME_TOKEN_PUSHER"
    }
}