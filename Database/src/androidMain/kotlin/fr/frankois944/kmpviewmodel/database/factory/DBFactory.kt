@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.factory

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import fr.frankois944.kmpviewmodel.database.room.dbFileName
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory
public actual class DBFactory(private val app: Context) {
    public actual fun createRoomDatabase(): AppDatabase {
        val dbFile = app.getDatabasePath(dbFileName)
        return Room.databaseBuilder<AppDatabase>(
            context = app,
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

@Single
public actual class PlatformHelper(
    public val context: Context
){
    public actual fun getName(): String = "I'm Android - $context"
}

@Single
public actual class PlatformHelper2 {
    public actual fun getName(): String = "I'm Android - NO CONTEXT"
}