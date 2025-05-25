package PGR208.exam.edamamapp.Database_searchHistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history_entity")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image: String,
    val label: String,
    val dietLabel: String,
    val healthLabel: String,
    val mealType: String,
    val url: String
)

