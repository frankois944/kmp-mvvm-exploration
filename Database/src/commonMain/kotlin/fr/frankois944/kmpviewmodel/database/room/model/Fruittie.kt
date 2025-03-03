package fr.frankois944.kmpviewmodel.database.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
public data class Fruittie(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @SerialName("name")
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("calories")
    val calories: String,
)
