package fr.frankois944.kmpviewmodel.viewmodels.mainscreen

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public open class ScreenUiEvent(
    timestamp: Instant = Clock.System.now(),
)

public sealed class MyFirstScreenUiEvents : ScreenUiEvent() {
    public class Retry : MyFirstScreenUiEvents()

    public class UpdateUserId(
        public val value: String,
    ) : MyFirstScreenUiEvents()

    public class NextView : MyFirstScreenUiEvents()
}
