@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.factory

import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import org.koin.core.annotation.Factory

@Factory
internal expect class DBFactory() {
    fun createRoomDatabase(): AppDatabase
}
