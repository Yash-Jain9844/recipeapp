package PGR208.exam.edamamapp.Database_searchHistory

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert
    suspend fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Update
    suspend fun update(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    suspend fun delete(searchHistoryEntity: SearchHistoryEntity)

    @Query("SELECT * FROM search_history_entity")
    suspend fun fetchAllOnce(): List<SearchHistoryEntity>

    @Query("DELETE FROM search_history_entity")
    suspend fun deleteAll()

    @Query("SELECT * FROM search_history_entity ORDER BY id DESC")
    fun fetchAllSearchHistoryEntities(): kotlinx.coroutines.flow.Flow<List<SearchHistoryEntity>>




}
