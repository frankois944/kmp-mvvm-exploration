package fr.frankois944.kmpviewmodel.datastore

import android.content.Context
import fr.frankois944.kmpviewmodel.platform.IPlatform
import okio.Path
import okio.Path.Companion.toPath

internal actual fun getDataStorePath(
    platform: IPlatform,
    fileName: String,
): Path = (platform.platformContext as Context).filesDir.resolve(fileName).absolutePath.toPath()
