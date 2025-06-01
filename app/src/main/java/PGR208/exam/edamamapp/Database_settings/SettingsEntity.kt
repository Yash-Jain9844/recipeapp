package PGR208.exam.edamamapp.Database_settings

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity class representing app settings in the database
@Entity(tableName = "settings_entity")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for the settings entry
    val desiredDiet: String, // Preferred diet
    val mealPriority: String, // Preferred meal priority
    val maxSearchHistoryItems: Int, // Maximum number of search history items
    val themePreference: String = "system" // Theme preference (light, dark, system)
)