package fr.frankois944.kmpviewmodel.models.services.account

import fr.frankois944.kmpviewmodel.models.dto.AccountData
import fr.frankois944.kmpviewmodel.models.dto.FruitData
import kotlinx.coroutines.flow.Flow

public interface IAccountService {

    @Throws(Exception::class)
    public fun getAllFruitsAsFlow(): Flow<List<FruitData>>

    @Throws(Exception::class)
    public suspend fun addFruit(name: String, fullName: String, calories: String)

    @Throws(Exception::class)
    public suspend fun removeAllFruit()
}
