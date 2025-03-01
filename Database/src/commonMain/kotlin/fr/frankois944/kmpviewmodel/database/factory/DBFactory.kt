@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.factory

import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

/*
di/FactoryModule
@Module
@ComponentScan("fr.frankois944.kmpviewmodel.database.factory")
public class FactoryModule
 */

// NOT WORKING
@Factory
public expect class DBFactory {
    public fun createRoomDatabase() : AppDatabase
}

// NOT WORKING WITH ANDROID CONTEXT
@Single
public expect class PlatformHelper {
    public fun getName() : String
}

// NOT WORKING WITHOUT ANDROID CONTEXT
@Single
public expect class PlatformHelper2 {
    public fun getName() : String
}

// WORKING
@Single
public class PlatformHelper3 {
    public fun getName() : String = "TEST"
}