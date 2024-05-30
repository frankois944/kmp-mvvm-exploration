package fr.frankois944.kmpviewmodel.models.services.account

import fr.frankois944.kmpviewmodel.models.dto.AccountData

public interface IAccountService {
    @Throws(Exception::class)
    public suspend fun getAccountInfo(): AccountData
}
