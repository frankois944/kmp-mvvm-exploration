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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.helpers.eventbus.AppEvents
import fr.frankois944.kmpviewmodel.helpers.eventbus.IEventBus
import fr.frankois944.kmpviewmodel.models.dto.AccountData
import fr.frankois944.kmpviewmodel.models.dto.ProfileData
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenUIState
import fr.frankois944.kmpviewmodel.viewmodels.mainscreen.MainScreenViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun MyFirstScreen(
    modifier: Modifier = Modifier,
    param1: String? = null,
    viewModel: MainScreenViewModel = koinViewModel(parameters = { parametersOf(param1) }),
    eventBus: IEventBus = koinInject(),
    logger: Logger = koinInject(parameters = { parametersOf("MyFirstScreen") }),
    onNextView: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        eventBus.subscribeEvent<AppEvents>().collect {
            logger.d("EVENT RECEIVED : $it")
        }
    }

    val mainScreenUIState by viewModel.mainScreenUIState.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    MyFirstView(
        modifier = modifier.fillMaxSize(),
        mainScreenUIState = mainScreenUIState,
        onNextView = onNextView,
        userId = userId,
        updateUserId = { viewModel.updateUserId() },
        retry = {
            viewModel.reload()
        },
    )
}

@Composable
fun MyFirstView(
    modifier: Modifier = Modifier,
    mainScreenUIState: MainScreenUIState,
    userId: String?,
    updateUserId: () -> Unit = {},
    onNextView: () -> Unit = {},
    retry: () -> Unit = {},
) {
    Column(modifier = modifier) {
        when (mainScreenUIState) {
            is MainScreenUIState.Error -> {
                Text(
                    text = "Error : ${mainScreenUIState.message}",
                    color = Color.Red,
                )
                Button(onClick = { retry() }) {
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
                    Button(onClick = updateUserId) {
                        Text(text = "RANDOM")
                    }
                    Text(
                        text = "Vos transactions",
                    )
                    LazyColumn {
                        items(items = data.account.transaction) { transaction ->
                            Text(
                                text = transaction,
                                fontWeight = FontWeight.SemiBold,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onNextView()
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
                updateUserId = { },
                onNextView = { },
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
                updateUserId = { },
                onNextView = { },
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
                        account = AccountData(transaction = listOf("Tr1", "Tr2")),
                    ),
                userId = "sdffds",
                updateUserId = { },
                onNextView = { },
            )
        }
    }
}
