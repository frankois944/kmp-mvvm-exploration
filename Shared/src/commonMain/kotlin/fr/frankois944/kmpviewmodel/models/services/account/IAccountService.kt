package fr.frankois944.kmpviewmodel.models.services.account

import fr.frankois944.kmpviewmodel.models.dto.FruitData
import kotlinx.coroutines.flow.Flow

public interface IAccountService {
    public fun getAllFruitsAsFlow(): Flow<List<FruitData>>

    public suspend fun addFruit(
        name: String,
        fullName: String,
        calories: String,
    )

    public suspend fun removeAllFruit()
}
