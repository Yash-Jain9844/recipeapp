package PGR208.exam.edamamapp.Database_settings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert
    suspend fun insert(settingsEntity: SettingsEntity)

    @Update
    suspend fun update(settingsEntity: SettingsEntity)

//    @Delete
//    suspend fun delete(settingsEntity: SettingsEntity)

    @Query("SELECT `calorieIntake` FROM `settings-table` WHERE id=1")
    fun fetchCalorieIntake(): Flow<Int>

    @Query("SELECT `maxSearchHistoryItems` FROM `settings-table` WHERE id=1")
    fun fetchMaxSearchHistoryItems(): Flow<Int>

    @Query("SELECT `desiredDiet` FROM `settings-table`WHERE id=1")
    fun fetchDesiredDiet(): Flow<String>

    @Query("SELECT `mealPriority` FROM `settings-table`WHERE id=1")
    fun fetchMealPriority(): Flow<String>
}