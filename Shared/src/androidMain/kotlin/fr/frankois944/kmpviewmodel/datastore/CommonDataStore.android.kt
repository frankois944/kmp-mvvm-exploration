package fr.frankois944.kmpviewmodel.datastore

import android.content.Context
import fr.frankois944.kmpviewmodel.platform.IPlatform
import okio.Path.Companion.toPath

internal actual fun getDataStorePath(
    platform: IPlatform,
    fileName: String,
) = requireNotNull(platform.platformContext as? Context) {
    "No Android context available"
}.filesDir.resolve(fileName).absolutePath.toPath()
