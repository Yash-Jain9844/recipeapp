package PGR208.exam.edamamapp.Database_settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_entity")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "desiredDiet")
    val desiredDiet: String = "None",

    @ColumnInfo(name = "mealPriority")
    val mealPriority: String = "None",

    @ColumnInfo(name = "maxSearchHistoryItems")
    val maxSearchHistoryItems: Int = 10
)
