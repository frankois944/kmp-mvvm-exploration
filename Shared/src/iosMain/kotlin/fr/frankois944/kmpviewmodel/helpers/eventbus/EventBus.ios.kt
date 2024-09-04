package fr.frankois944.kmpviewmodel.helpers.eventbus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSNotificationCenter

internal actual fun EventBus.publishNotification(
    event: AppEvents,
    content: Any?,
) = runBlocking(Dispatchers.Main) {
    logger.d("post iOS native notification to app : $event - $content")
    NSNotificationCenter.defaultCenter.postNotificationName(
        aName = event.name,
        `object` = content,
    )
}
