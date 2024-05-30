package fr.frankois944.kmpviewmodel.models.context.preferences

import kotlinx.coroutines.flow.Flow

public interface IPersistingData {
    public val usernameFlow: Flow<String?>
    public var username: String?

    public val isEnabledFlow: Flow<Boolean?>
    public var isEnabled: Boolean
}
