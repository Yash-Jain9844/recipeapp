package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoritesDao {

    @Insert
    suspend fun insert(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun delete(favoritesEntity: FavoritesEntity)

    // ✅ Check if a meal is already in favorites
    @Query("SELECT EXISTS(SELECT 1 FROM favorites_entity WHERE label = :label)")
    suspend fun isFavorite(label: String): Boolean

    // ✅ Optional: Get all favorites (for displaying in Favorites screen)
    @Query("SELECT * FROM favorites_entity")
    suspend fun getAllFavorites(): List<FavoritesEntity>
}
