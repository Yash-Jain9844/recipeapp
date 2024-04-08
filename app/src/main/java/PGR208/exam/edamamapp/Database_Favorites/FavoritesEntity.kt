package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites-table")
data class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var recipeName: String = ""
        )