package PGR208.exam.edamamapp.Database_settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object for settings database operations
@Dao
interface SettingsDao {

    // Insert a settings entity
    @Insert
    suspend fun insert(settingsEntity: SettingsEntity)

    // Fetch desired diet as a Flow
    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    fun fetchDesiredDiet(): Flow<String?>

    // Fetch desired diet once
    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    suspend fun fetchDesiredDietOnce(): String?

    // Fetch meal priority as a Flow
    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    fun fetchMealPriority(): Flow<String?>

    // Fetch meal priority once
    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    suspend fun fetchMealPriorityOnce(): String?

    // Fetch max search history items as a Flow
    @Query("SELECT maxSearchHistoryItems FROM settings_entity LIMIT 1")
    fun fetchMaxSearchHistoryItems(): Flow<Int?>

    // Get the number of rows in the settings table
    @Query("SELECT COUNT(*) FROM settings_entity")
    suspend fun getRowCount(): Int

    // Fetch theme preference
    @Query("SELECT themePreference FROM settings_entity LIMIT 1")
    suspend fun fetchThemePreference(): String?

    // Update theme preference
    @Query("UPDATE settings_entity SET themePreference = :themePreference")
    suspend fun updateThemePreference(themePreference: String)
}