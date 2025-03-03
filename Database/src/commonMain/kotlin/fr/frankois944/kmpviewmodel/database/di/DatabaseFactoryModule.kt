package fr.frankois944.kmpviewmodel.database.di

import fr.frankois944.kmpviewmodel.database.factory.DBFactory
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("fr.frankois944.kmpviewmodel.database.room")
public class DatabaseFactoryModule {
    internal val dbFactory: DBFactory = DBFactory()

    // @Factory
    // internal fun dbFactory(): DBFactory = DBFactory()

    @Single
    internal fun database(): AppDatabase = dbFactory.createRoomDatabase()
}
