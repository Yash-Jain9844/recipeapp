package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// Data Access Object for favorites database operations
@Dao
interface FavoritesDao {

    // Insert a favorite entity into the database
    @Insert
    suspend fun insert(favoritesEntity: FavoritesEntity)

    // Delete a favorite entity from the database
    @Delete
    suspend fun delete(favoritesEntity: FavoritesEntity)

    // Check if a recipe is a favorite by label
    @Query("SELECT EXISTS(SELECT 1 FROM favorites_entity WHERE label = :label)")
    suspend fun isFavorite(label: String): Boolean

    // Retrieve all favorite entities
    @Query("SELECT * FROM favorites_entity")
    suspend fun getAllFavorites(): List<FavoritesEntity>

    // Delete all favorite entities
    @Query("DELETE FROM favorites_entity")
    suspend fun deleteAll()
}