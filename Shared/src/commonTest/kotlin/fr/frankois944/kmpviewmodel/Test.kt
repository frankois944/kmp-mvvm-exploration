package fr.frankois944.kmpviewmodel

import app.cash.turbine.test
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenUIState
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonGreetingTest {
    @ExperimentalCoroutinesApi
    @BeforeTest
    fun beforeTest() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @ExperimentalCoroutinesApi
    @AfterTest
    fun afterTest() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testExample() =
        runTest {
            val koinScope = startApp(appConfig = AppConfig(isDebug = false, isProduction = false))
            val viewModel: MainScreenViewModel = koinScope.koin.get()
            viewModel.mainScreenUIState.test {
                assertTrue(
                    awaitItem() is MainScreenUIState.Loading,
                    "Not good initial status",
                )
                assertTrue(
                    awaitItem() is MainScreenUIState.Success,
                    "The status should be success",
                )
                assertEquals(
                    "Franck",
                    (viewModel.mainScreenUIState.value as MainScreenUIState.Success).profile.username,
                )
            }
        }
}
