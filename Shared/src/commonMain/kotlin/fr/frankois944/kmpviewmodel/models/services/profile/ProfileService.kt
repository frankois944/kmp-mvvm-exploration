package fr.frankois944.kmpviewmodel.models.services.profile

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.logs.log
import fr.frankois944.kmpviewmodel.models.dto.ProfileData
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
internal class ProfileService(
    val logger: Logger = log("ProfileService"),
) : IProfileService, KoinComponent {
    override suspend fun getProfile(): ProfileData {
        logger.d("Load")
        return ProfileData(username = "Franck")
    }
}
