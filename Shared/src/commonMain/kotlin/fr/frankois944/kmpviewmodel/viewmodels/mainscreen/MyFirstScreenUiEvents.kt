package fr.frankois944.kmpviewmodel.viewmodels.mainscreen

public sealed class MyFirstScreenUiEvents {
    public data object Retry : MyFirstScreenUiEvents()

    public class UpdateUserId(
        value: String,
    ) : MyFirstScreenUiEvents()

    public data object NextView : MyFirstScreenUiEvents()
}
