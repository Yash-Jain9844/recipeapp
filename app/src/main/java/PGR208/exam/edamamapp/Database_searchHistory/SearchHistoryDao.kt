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

    @Query("DELETE FROM `search-history-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `search-history-table`")
    fun fetchAllSearchHistoryEntities(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM `search-history-table` WHERE id = :id")
    fun deleteById(id: Int)
}
