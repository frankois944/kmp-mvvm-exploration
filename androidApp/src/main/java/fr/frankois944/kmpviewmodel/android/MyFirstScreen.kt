package fr.frankois944.kmpviewmodel.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.models.dto.AccountData
import fr.frankois944.kmpviewmodel.models.dto.FruitData
import fr.frankois944.kmpviewmodel.models.dto.ProfileData
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenUIState
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenViewModel
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MyFirstScreenUiEvents
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun MyFirstScreen(
    modifier: Modifier = Modifier,
    param1: String? = null,
    viewModel: MainScreenViewModel = koinViewModel(parameters = { parametersOf(param1) }),
    logger: Logger = koinInject(parameters = { parametersOf("MyFirstScreen") }),
    onNextView: () -> Unit,
) {
    val mainScreenUIState by viewModel.mainScreenUIState.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val fruits by viewModel.datasource.collectAsStateWithLifecycle()

    MyFirstView(
        modifier = modifier.fillMaxSize(),
        mainScreenUIState = mainScreenUIState,
        userId = userId,
        fruits = fruits,
        events = {
            when (it) {
                is MyFirstScreenUiEvents.NextView -> onNextView()
                is MyFirstScreenUiEvents.Retry -> viewModel.reload()
                is MyFirstScreenUiEvents.UpdateUserId -> viewModel.updateUserId()
            }
        },
    )
}

@Composable
fun MyFirstView(
    modifier: Modifier = Modifier,
    mainScreenUIState: MainScreenUIState,
    userId: String?,
    fruits: List<FruitData>,
    events: (MyFirstScreenUiEvents) -> Unit = {},
) {
    Column(modifier = modifier) {
        when (mainScreenUIState) {
            is MainScreenUIState.Error -> {
                Text(
                    text = "Error : ${mainScreenUIState.message}",
                    color = Color.Red,
                )
                Button(onClick = { events(MyFirstScreenUiEvents.Retry()) }) {
                    Text(text = "RETRY")
                }
            }

            MainScreenUIState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            is MainScreenUIState.Success -> {
                val data by remember {
                    derivedStateOf { mainScreenUIState }
                }

                Column {
                    Row {
                        Text(
                            text = "Bonjour $userId: ",
                        )
                        Text(
                            text = data.profile.username,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Button(onClick = { events(MyFirstScreenUiEvents.UpdateUserId("42")) }) {
                        Text(text = "RANDOM")
                    }
                    Text(
                        text = "Vos transactions",
                    )
                    LazyColumn {
                        items(items = fruits) { fruit ->
                            Text(
                                text = fruit.fullName,
                                fontWeight = FontWeight.SemiBold,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            events(MyFirstScreenUiEvents.NextView())
                                        },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MyFirstPreviewViewLoading() {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MyFirstView(
                mainScreenUIState = MainScreenUIState.Loading,
                userId = "sqd",
                fruits = emptyList()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MyFirstPreviewViewError() {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MyFirstView(
                mainScreenUIState = MainScreenUIState.Error("An error"),
                userId = "sdfsdf",
                fruits = emptyList()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MyFirstPreviewViewSuccess() {
    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MyFirstView(
                mainScreenUIState =
                    MainScreenUIState.Success(
                        profile = ProfileData("Joker"),
                    ),
                userId = "sdffds",
                fruits = emptyList()
            )
        }
    }
}
