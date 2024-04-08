package PGR208.exam.edamamapp.Database_settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings-table")
data class SettingsEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var calorieIntake: Int = 0,

    var maxSearchHistoryItems: Int = 0,

    var desiredDiet: String = "",

    var mealPriority: String = ""
    )