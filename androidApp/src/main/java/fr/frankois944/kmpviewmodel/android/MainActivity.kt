@file:OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)

package fr.frankois944.kmpviewmodel.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.frankois944.kmpviewmodel.logs.log
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinAndroidContext {
                // Get current navigation context
                val navController = rememberNavController()
                var canGoBack by remember { mutableStateOf(false) }
                val logger = log("MainActivity")

                MyApplicationTheme {
                    val scrollBehavior =
                        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                    val bottomScrollBehavior =
                        BottomAppBarDefaults.exitAlwaysScrollBehavior(rememberBottomAppBarState())

                    Scaffold(
                        modifier =
                            Modifier
                                .nestedScroll(
                                    scrollBehavior.nestedScrollConnection,
                                ).nestedScroll(
                                    bottomScrollBehavior.nestedScrollConnection,
                                ),
                        topBar = {
                            CenterAlignedTopAppBar(
                                scrollBehavior = scrollBehavior,
                                colors =
                                    TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                    ),
                                title = {
                                    Text("Top App Bar")
                                },
                                navigationIcon = {
                                    if (canGoBack) {
                                        IconButton(onClick = {
                                            logger.d("PRESS navigateUp")
                                            navController.navigateUp()
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Localized description",
                                            )
                                        }
                                    }
                                },
                            )
                        },
                        bottomBar = {
                            BottomAppBar(actions = {}, scrollBehavior = bottomScrollBehavior)
                        },
                    ) {
                        NavHost(
                            modifier = Modifier.padding(it),
                            navController = navController,
                            startDestination = "MyFirstScreen",
                        ) {
                            composable(
                                "MyFirstScreen",
                            ) {
                                canGoBack = false
                                MyFirstScreen {
                                    logger.d("Trigger Navigate to NextScreen/TOTO")
                                    navController.navigate("NextScreen/TOTO")
                                }
                            }
                            composable("NextScreen/{value}") { nav ->
                                canGoBack = true
                                MyFirstScreen(param1 = nav.arguments!!.getString("value")!!) {
                                    logger.d("Trigger Navigate to NextScreen/PLOP")
                                    navController.navigate("NextScreen/PLOP")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
