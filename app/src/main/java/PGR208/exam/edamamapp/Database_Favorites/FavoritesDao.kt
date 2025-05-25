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

    @Query("SELECT EXISTS(SELECT 1 FROM favorites_entity WHERE label = :label)")
    suspend fun isFavorite(label: String): Boolean

    @Query("SELECT * FROM favorites_entity")
    suspend fun getAllFavorites(): List<FavoritesEntity>

    @Query("DELETE FROM favorites_entity")
    suspend fun deleteAll()
}