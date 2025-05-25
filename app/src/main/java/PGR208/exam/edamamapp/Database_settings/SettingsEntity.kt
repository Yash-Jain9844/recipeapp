package PGR208.exam.edamamapp.Database_settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_entity")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val desiredDiet: String,
    val mealPriority: String,
    val maxSearchHistoryItems: Int,
    val themePreference: String = "system" // Default to system
)