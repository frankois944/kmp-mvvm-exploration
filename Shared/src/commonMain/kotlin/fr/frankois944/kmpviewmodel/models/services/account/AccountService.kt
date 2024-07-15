package fr.frankois944.kmpviewmodel.models.services.account

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.models.dto.AccountData
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parameterSetOf

@Single
internal class AccountService :
    IAccountService,
    KoinComponent {
    val logger: Logger = get(parameters = { parameterSetOf("AccountService") })

    @Throws(Exception::class)
    override suspend fun getAccountInfo(): AccountData {
        logger.d("Load")
        return AccountData(
            List(50) { index -> "Hello $index" },
        )
    }
}
