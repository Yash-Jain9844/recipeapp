package PGR208.exam.edamamapp.Database_Favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface FavoritesDao {

    @Insert
    suspend fun insert(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun delete(favoritesEntity: FavoritesEntity)

}