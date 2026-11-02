package io.github.vedantjoshi23.kairos.core.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * In-process event bus (ARCH-SYSTEM §3). It carries follow-up notifications between
 * contexts only; nothing on the alarm safety path depends on it.
 */
class EventBus {
    private val flow = MutableSharedFlow<Any>(extraBufferCapacity = 64)
    val events: SharedFlow<Any> = flow.asSharedFlow()

    fun publish(event: Any): Boolean = flow.tryEmit(event)
}
