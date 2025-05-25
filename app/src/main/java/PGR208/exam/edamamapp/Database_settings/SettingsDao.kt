package PGR208.exam.edamamapp.Database_settings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settingsEntity: SettingsEntity)

    @Query("SELECT maxSearchHistoryItems FROM settings_entity LIMIT 1")
    fun fetchMaxSearchHistoryItems(): Flow<Int?>

    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    fun fetchDesiredDiet(): Flow<String?>

    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    fun fetchMealPriority(): Flow<String?>

    // New suspend functions to fetch settings values once (not as Flow)
    @Query("SELECT desiredDiet FROM settings_entity LIMIT 1")
    suspend fun fetchDesiredDietOnce(): String?

    @Query("SELECT mealPriority FROM settings_entity LIMIT 1")
    suspend fun fetchMealPriorityOnce(): String?

    @Query("SELECT COUNT(*) FROM settings_entity")
    suspend fun getRowCount(): Int

    @Update
    suspend fun update(settingsEntity: SettingsEntity)
}
