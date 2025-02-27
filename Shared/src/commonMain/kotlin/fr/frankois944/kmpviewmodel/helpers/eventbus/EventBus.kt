@file:OptIn(ExperimentalMultiplatform::class)

package fr.frankois944.kmpviewmodel.helpers.eventbus

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Logger.Companion.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

internal expect fun EventBus.publishNotification(
    event: AppEvents,
    content: Any?,
)

// internal inline fun <reified T : AppEvents> EventBus.subscribe(): Flow<Pair<T, Any?>> = subscribeEvent()

@Single
internal class EventBus :
    IEventBus,
    KoinComponent {
    private val coroutineContext = CoroutineScope(Dispatchers.Default)
    internal val logger: Logger by inject(parameters = { parametersOf("EventBus") })

    private val events: MutableSharedFlow<Pair<AppEvents, Any?>> = MutableSharedFlow()

    override fun publish(
        name: AppEvents,
        value: Any?,
    ) {
        logger.d("Publish event $name $value")
        coroutineContext.launch {
            // send the notification to the Kotlin compatible API
            events.emit(Pair(name, value))
            // Send the notification to the native API
            publishNotification(name, value)
        }
    }

    override fun <T : AppEvents> subscribeEvent(): Flow<Pair<T, Any?>> =
        channelFlow {
            launch(Dispatchers.Default) {
                events.collectLatest { event ->
                    if (coroutineContext.isActive) {
                        logger.d("forwarding event $event")
                        @Suppress("UNCHECKED_CAST")
                        send(event as Pair<T, Any?>)
                    }
                }
            }
        }
}
