package fr.frankois944.kmpviewmodel

import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenUIState
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonGreetingTest {
    private suspend fun waitUntil(
        timeout: Long = 2000,
        interval: Long = 500,
        message: String = "",
        condition: suspend () -> Boolean,
    ) {
        withContext(Dispatchers.Default) {
            var currentTime = 0L
            while (!condition()) {
                if (currentTime >= timeout) {
                    assertTrue(currentTime < timeout, "[waitUntil timeout]Reason:$message")
                }
                delay(interval)
                currentTime += interval
            }
        }
    }

    @ExperimentalCoroutinesApi
    @BeforeTest
    fun beforeTest() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @ExperimentalCoroutinesApi
    @AfterTest
    fun afterTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExample() =
        runTest {
            startApp(appConfig = AppConfig(isDebug = false, isProduction = false))
            val viewModel = MainScreenViewModel()
            assertTrue(
                viewModel.mainScreenUIState.value is MainScreenUIState.Loading,
                "Not good initial status",
            )
            viewModel.mainScreenUIState.launchIn(this.backgroundScope)
            waitUntil {
                viewModel.mainScreenUIState.value is MainScreenUIState.Success
            }
            assertEquals(
                "Franck",
                (viewModel.mainScreenUIState.value as MainScreenUIState.Success).profile.username,
            )
        }
}
