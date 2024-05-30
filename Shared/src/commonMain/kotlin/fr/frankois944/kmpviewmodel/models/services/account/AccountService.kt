package fr.frankois944.kmpviewmodel.models.services.account

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.logs.log
import fr.frankois944.kmpviewmodel.models.dto.AccountData
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Single
internal class AccountService(
    val logger: Logger = log("AccountService"),
) : IAccountService, KoinComponent {
    override suspend fun getAccountInfo(): AccountData {
        logger.d("Load")
        return AccountData(
            List(50) { index -> "Hello $index" },
        )
    }
}
