package ru.netology.nmedia.data.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun String.formatDate(): String {
    val formatter = SimpleDateFormat.getDateTimeInstance()
    return formatter.format(Date(this.toLong() * 1000))
}

fun<T> commandSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 10,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun<T> Flow<T>.observeSharedFlow(lifecycleOwner: LifecycleOwner, body: (T) -> Unit) {
    lifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
        collectLatest {
            body(it)
        }
    }
}

fun<T> Flow<T>.observeStateFlow(lifecycleOwner: LifecycleOwner, body: (T) -> Unit) {
    lifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
        collect {
            body(it)
        }
    }
}

private fun LifecycleOwner.addRepeatingJob(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(state, block)
    }
}
