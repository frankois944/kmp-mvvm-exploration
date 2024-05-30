package fr.frankois944.kmpviewmodel.android.di

import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
internal class AndroidModule {
    @KoinViewModel
    fun mainScreenViewModel(parameters: String? = null): MainScreenViewModel {
        return MainScreenViewModel(param1 = parameters)
    }
}
