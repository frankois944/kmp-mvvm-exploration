package fr.frankois944.kmpviewmodel.preferences

import fr.frankois944.kmpviewmodel.platform.IPlatform
import okio.Path

@Suppress("ktlint:standard:property-naming")
internal const val DataStoreFileName = "common.preferences_pb"

// get the native datastore PATH
internal expect fun getDataStorePath(
    platform: IPlatform,
    fileName: String = DataStoreFileName,
): Path
