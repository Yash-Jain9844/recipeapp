package PGR208.exam.edamamapp.Database_searchHistory

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity class representing a search history entry in the database
@Entity(tableName = "search_history_entity")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for the search history entry
    val image: String, // Recipe image URL
    val label: String, // Recipe name
    val dietLabel: String, // Diet category
    val healthLabel: String, // Health information
    val mealType: String, // Meal type
    val url: String // Recipe source URL
)