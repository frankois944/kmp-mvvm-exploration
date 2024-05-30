package fr.frankois944.kmpviewmodel.helpers.eventbus

import kotlinx.coroutines.flow.Flow

public interface IEventBus {
    public fun publish(
        name: AppEvents,
        value: Any? = null,
    )

    public fun <T : AppEvents> subscribeEvent(): Flow<Pair<T, Any?>>
}
