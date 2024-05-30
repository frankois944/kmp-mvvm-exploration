package fr.frankois944.kmpviewmodel.datastore

import fr.frankois944.kmpviewmodel.platform.IPlatform
import okio.Path

@Suppress("ktlint:standard:property-naming")
internal const val DataStoreFileName = "common.preferences_pb"

internal expect fun getDataStorePath(
    platform: IPlatform,
    fileName: String = DataStoreFileName,
): Path
