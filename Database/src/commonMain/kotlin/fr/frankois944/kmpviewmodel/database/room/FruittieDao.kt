package fr.frankois944.kmpviewmodel.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.frankois944.kmpviewmodel.database.room.model.Fruittie
import kotlinx.coroutines.flow.Flow

@Dao
public interface FruittieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public suspend fun insert(fruittie: Fruittie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public suspend fun insert(fruitties: List<Fruittie>)

    @Query("SELECT * FROM Fruittie")
    public fun getAllAsFlow(): Flow<List<Fruittie>>

    @Query("SELECT COUNT(*) as count FROM Fruittie")
    public suspend fun count(): Int

    @Query("SELECT * FROM Fruittie WHERE id in (:ids)")
    public suspend fun loadAll(ids: List<Long>): List<Fruittie>

    @Query("SELECT * FROM Fruittie WHERE id in (:ids)")
    public suspend fun loadMapped(ids: List<Long>): Map<
            @MapColumn(columnName = "id")
            Long,
            Fruittie,
            >

    @Query("DELETE FROM Fruittie")
    public suspend fun deleteAll()
}