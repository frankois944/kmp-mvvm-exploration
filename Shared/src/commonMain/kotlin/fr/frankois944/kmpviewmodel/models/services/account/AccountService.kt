package fr.frankois944.kmpviewmodel.models.services.account

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.database.room.AppDatabase
import fr.frankois944.kmpviewmodel.database.room.model.Fruittie
import fr.frankois944.kmpviewmodel.models.dto.FruitData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parameterSetOf

@Single
internal class AccountService(
    @Provided private val database: AppDatabase,
) : IAccountService,
    KoinComponent {
    val logger: Logger = get(parameters = { parameterSetOf("AccountService") })

    init {
        logger.d("Init")
    }

    override fun getAllFruitsAsFlow(): Flow<List<FruitData>> =
        database.fruittieDao().getAllAsFlow().map {
            it.map {
                FruitData(it.name, it.fullName, it.calories)
            }
        }

    override suspend fun addFruit(
        name: String,
        fullName: String,
        calories: String,
    ) {
        database
            .fruittieDao()
            .insert(Fruittie(name = name, fullName = fullName, calories = calories))
    }

    override suspend fun removeAllFruit() {
        database.fruittieDao().deleteAll()
    }
}
