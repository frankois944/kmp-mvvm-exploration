package fr.frankois944.kmpviewmodel.di

import fr.frankois944.kmpviewmodel.database.di.DatabaseFactoryModule
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(includes = [DatabaseFactoryModule::class])
@Configuration
internal class DataBaseModule

@KoinApplication
internal object KoinApp
