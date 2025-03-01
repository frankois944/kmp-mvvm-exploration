package fr.frankois944.kmpviewmodel.database.di

import fr.frankois944.kmpviewmodel.database.factory.DBFactory
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [FactoryModule::class])
@ComponentScan("fr.frankois944.kmpviewmodel.database")
public class DatabaseModule {
    @Single
    public fun database(factory: DBFactory): AppDatabase = factory.createRoomDatabase()
}
