package PGR208.exam.edamamapp.Database_remainingCalories

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remaining-calories")
data class RemainingCaloriesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1,
    var remainigCalories: Int = 0
        )