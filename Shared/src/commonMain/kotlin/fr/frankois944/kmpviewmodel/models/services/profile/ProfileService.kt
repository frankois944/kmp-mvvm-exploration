package fr.frankois944.kmpviewmodel.models.services.profile

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.models.dto.ProfileData
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parameterSetOf

@Single
internal class ProfileService :
    IProfileService,
    KoinComponent {
    val logger: Logger = get(parameters = { parameterSetOf("ProfileService") })

    @Throws(Exception::class)
    override suspend fun getProfile(): ProfileData {
        logger.d("Load")
        return ProfileData(username = "Franck")
    }
}
