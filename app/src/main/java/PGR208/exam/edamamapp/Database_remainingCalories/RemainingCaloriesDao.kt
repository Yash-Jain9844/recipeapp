package PGR208.exam.edamamapp.Database_remainingCalories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RemainingCaloriesDao {

    @Insert
    suspend fun insert(remainingCaloriesEntity: RemainingCaloriesEntity)

    @Update
    suspend fun update(remainingCaloriesEntity: RemainingCaloriesEntity)

    @Query("SELECT `remainigCalories` FROM `remaining-calories` WHERE id=1")
    fun fetchRemainingCalories(): Flow<Int>
}