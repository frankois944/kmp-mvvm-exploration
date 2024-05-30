package fr.frankois944.kmpviewmodel.viewmodels.mainscreen

import fr.frankois944.kmpviewmodel.models.dto.AccountData
import fr.frankois944.kmpviewmodel.models.dto.ProfileData

public sealed class MainScreenUIState {
    public data object Idle : MainScreenUIState()

    public data object Loading : MainScreenUIState()

    public data class Error(val message: String) : MainScreenUIState()

    public data class Success(val profile: ProfileData, val account: AccountData) : MainScreenUIState()
}
