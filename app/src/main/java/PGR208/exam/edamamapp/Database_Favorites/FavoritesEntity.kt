package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity class representing a favorite recipe in the database
@Entity(tableName = "favorites_entity")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for the favorite
    val label: String, // Recipe name
    val image: String = "", // Recipe image URL
    val dietLabel: String = "", // Diet category
    val healthLabel: String = "", // Health information
    val mealType: String = "", // Meal type
    val url: String = "" // Recipe source URL
)