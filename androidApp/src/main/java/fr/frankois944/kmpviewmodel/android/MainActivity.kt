@file:OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)

package fr.frankois944.kmpviewmodel.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinAndroidContext {
                MyApplicationTheme {
                    val navController = rememberNavController()
                    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                    val bottomScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior(rememberBottomAppBarState())

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                    ) {
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
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Localized description",
                                            )
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
                                    MyFirstScreen {
                                        navController.navigate("MyFirstScreen2/TOTO")
                                    }
                                }
                                composable("MyFirstScreen2/{value}") { nav ->
                                    MyFirstScreen(param1 = nav.arguments!!.getString("value")!!) {
                                        navController.navigate("MyFirstScreen")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
