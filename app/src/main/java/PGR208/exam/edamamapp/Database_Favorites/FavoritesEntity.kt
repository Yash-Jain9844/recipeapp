package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_entity")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val image: String = "",
    val dietLabel: String = "",
    val healthLabel: String = "",
    val mealType: String = "",
    val url: String = ""
)