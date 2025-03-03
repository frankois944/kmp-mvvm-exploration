package fr.frankois944.kmpviewmodel.database.di

import fr.frankois944.kmpviewmodel.database.factory.DBFactory
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.ksp.generated.module

public val databaseModule: org.koin.core.module.Module =
    module {
        includes(DatabaseFactoryModule().module)
        factoryOf(::DBFactory)
    }

@Module
@ComponentScan("fr.frankois944.kmpviewmodel.database.room")
public class DatabaseFactoryModule {
    @Single
    public fun database(factory: DBFactory): AppDatabase = factory.createRoomDatabase()
}
