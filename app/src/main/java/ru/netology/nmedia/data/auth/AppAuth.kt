package ru.netology.nmedia.data.auth

import android.content.Context
import androidx.core.content.edit
import androidx.work.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.worker.SendPushTokenWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _state: MutableStateFlow<AuthState?>

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        _state = if (token == null || prefs.contains(ID_KEY).not()) {
            prefs.edit { clear() }
            MutableStateFlow(null)
        } else {
            MutableStateFlow(AuthState(id = id, token = token))
        }
    }

    val state = _state.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _state.tryEmit(AuthState(id = id, token = token))
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        val workManager = EntryPointAccessors
            .fromApplication<AppAuthEntryPoint>(context).getWorkManager()

        workManager.enqueueUniqueWork(
            SendPushTokenWorker.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<SendPushTokenWorker>()
                .setInputData(workDataOf(SendPushTokenWorker.TOKEN_KEY to token))
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .build()
        )
    }

    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _state.tryEmit(null)
        sendPushToken()
    }

    @[EntryPoint InstallIn(SingletonComponent::class)]
    interface AppAuthEntryPoint {
        fun getWorkManager(): WorkManager

    }

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"
    }
}