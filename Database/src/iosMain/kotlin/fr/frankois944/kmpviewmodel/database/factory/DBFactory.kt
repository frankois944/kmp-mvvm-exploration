@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.factory

import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import fr.frankois944.kmpviewmodel.database.room.dbFileName
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.IO
import org.koin.core.annotation.Factory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@Factory
internal actual class DBFactory {
    actual fun createRoomDatabase(): AppDatabase {
        val dbFile = "${fileDirectory()}/$dbFileName"
        return androidx.room.Room
            .databaseBuilder<AppDatabase>(
                name = dbFile,
            ).setDriver(androidx.sqlite.driver.NativeSQLiteDriver())
            .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
            .build()
    }

    private fun fileDirectory(): String {
        val documentDirectory: NSURL? =
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
        return requireNotNull(documentDirectory).path!!
    }
}
