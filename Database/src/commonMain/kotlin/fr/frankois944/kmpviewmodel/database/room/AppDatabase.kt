@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.database.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import fr.frankois944.kmpviewmodel.database.room.model.Fruittie

@Database(entities = [Fruittie::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
public abstract class AppDatabase : RoomDatabase() {
    public abstract fun fruittieDao(): FruittieDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
internal const val dbFileName = "fruits.db"

