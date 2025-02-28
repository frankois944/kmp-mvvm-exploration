@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database

import org.koin.core.annotation.Factory

@Factory
public expect class DBFactory {
    fun createRoomDatabase() : AppDatabase
}