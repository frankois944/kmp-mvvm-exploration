package fr.frankois944.kmpviewmodel.preferences

import kotlinx.coroutines.flow.Flow

// a interface to expose the data store
public interface IPersistingData {
    public val usernameFlow: Flow<String?>
    public var username: String?

    public val isEnabledFlow: Flow<Boolean?>
    public var isEnabled: Boolean
}
