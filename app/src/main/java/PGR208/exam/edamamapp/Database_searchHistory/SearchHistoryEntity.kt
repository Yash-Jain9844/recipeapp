package PGR208.exam.edamamapp.Database_searchHistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search-history-table")
data class SearchHistoryEntity (
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var image: String = "",
    var label: String = "",
    var dietLabel: String = "",
    var healthLabel: String = "",
    var mealType: String = "",
    var url : String = "",
    var calories: Double = 0.0,
    var yield: Int = 0
        )