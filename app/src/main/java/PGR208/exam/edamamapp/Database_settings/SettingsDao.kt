package PGR208.exam.edamamapp.Database_settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert
    suspend fun insert(settingsEntity: SettingsEntity)

    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    fun fetchDesiredDiet(): Flow<String?>

    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    suspend fun fetchDesiredDietOnce(): String?

    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    fun fetchMealPriority(): Flow<String?>

    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    suspend fun fetchMealPriorityOnce(): String?

    @Query("SELECT maxSearchHistoryItems FROM settings_entity LIMIT 1")
    fun fetchMaxSearchHistoryItems(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM settings_entity")
    suspend fun getRowCount(): Int

    @Query("SELECT themePreference FROM settings_entity LIMIT 1")
    suspend fun fetchThemePreference(): String?

    @Query("UPDATE settings_entity SET themePreference = :themePreference")
    suspend fun updateThemePreference(themePreference: String)
}