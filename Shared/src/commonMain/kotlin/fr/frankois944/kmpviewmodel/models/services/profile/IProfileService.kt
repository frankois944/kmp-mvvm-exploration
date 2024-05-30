package fr.frankois944.kmpviewmodel.models.services.profile

import fr.frankois944.kmpviewmodel.models.dto.ProfileData

public interface IProfileService {
    public suspend fun getProfile(): ProfileData
}
