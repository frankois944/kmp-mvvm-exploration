@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.factory

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import fr.frankois944.kmpviewmodel.database.room.dbFileName
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public actual class DBFactory actual constructor() : KoinComponent {
    private val app: Context by inject()

    public actual fun createRoomDatabase(): AppDatabase {
        val dbFile = app.getDatabasePath(dbFileName)
        return Room
            .databaseBuilder<AppDatabase>(
                context = app,
                name = dbFile.absolutePath,
            ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
