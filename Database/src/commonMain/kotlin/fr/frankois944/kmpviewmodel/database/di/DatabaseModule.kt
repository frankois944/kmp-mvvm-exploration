package fr.frankois944.kmpviewmodel.database.di

import fr.frankois944.kmpviewmodel.database.AppDatabase
import fr.frankois944.kmpviewmodel.database.DBFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("fr.frankois944.kmpviewmodel.database")
public class DatabaseModule {
    @Single
    public fun database(factory: DBFactory): AppDatabase {
        return factory.createRoomDatabase()
    }
}
