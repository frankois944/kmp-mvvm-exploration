package fr.frankois944.kmpviewmodel.router

import kotlinx.serialization.Serializable

// CommonMain contains only Route declarations
// it uses by iOS And Android native navigation in the same time.
public sealed class NavRoute {
    @Serializable
    public data object MainScreen

    // Define a profile route that takes an ID
    @Serializable
    public data class SecondScreen(
        val userId: String? = null,
    )
}
