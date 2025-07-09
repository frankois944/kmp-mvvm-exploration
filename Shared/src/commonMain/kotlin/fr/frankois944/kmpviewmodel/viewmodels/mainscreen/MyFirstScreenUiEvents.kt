@file:OptIn(ExperimentalTime::class)

package fr.frankois944.kmpviewmodel.viewmodels.mainscreen

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public open class ScreenUiEvent(
    timestamp: Instant = Clock.System.now(),
)

public sealed class MyFirstScreenUiEvents : ScreenUiEvent() {
    public class Retry : MyFirstScreenUiEvents()

    public class UpdateUserId(
        public val value: String,
    ) : MyFirstScreenUiEvents()

    public class NextView : MyFirstScreenUiEvents()

    public class AddNewFruit : MyFirstScreenUiEvents()

    public class RemoveAllFruit : MyFirstScreenUiEvents()
}
