package fr.frankois944.kmpviewmodel.preferences

import fr.frankois944.kmpviewmodel.platform.IPlatform
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
internal actual fun getDataStorePath(
    platform: IPlatform,
    fileName: String,
): Path {
    val documentDirectory: NSURL? =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return (requireNotNull(documentDirectory).path + "/$fileName").toPath()
}
