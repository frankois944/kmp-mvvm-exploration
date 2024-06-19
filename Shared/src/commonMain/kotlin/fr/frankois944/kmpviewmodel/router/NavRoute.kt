package fr.frankois944.kmpviewmodel.router

import kotlinx.serialization.Serializable

public sealed class NavRoute {
    @Serializable
    public data object MainScreen

    // Define a profile route that takes an ID
    @Serializable
    public data class SecondScreen(
        val userId: String? = null,
    )
}
