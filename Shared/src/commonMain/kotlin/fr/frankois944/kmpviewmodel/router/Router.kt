package fr.frankois944.kmpviewmodel.router

import org.koin.core.component.KoinComponent

public enum class Router(private val route: String) {
    MainScreen(route = "MainScreen"),
    MainScreen2(route = "MainScreen2/{userId}"),
    ;

    /*fun args(): List<NamedNavArgument> {
        return when (this) {
            MainScreen -> listOf()
            MainScreen2 -> listOf(navArgument("userId") { type = NavType.StringType })
        }
    }*/

    public companion object : KoinComponent {
        public val startDestination: Router = MainScreen
    }
}
