package PGR208.exam.edamamapp.Database_searchHistory

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Data Access Object for search history database operations
@Dao
interface SearchHistoryDao {

    // Insert a search history entity
    @Insert
    suspend fun insert(searchHistoryEntity: SearchHistoryEntity)

    // Update a search history entity
    @Update
    suspend fun update(searchHistoryEntity: SearchHistoryEntity)

    // Delete a search history entity
    @Delete
    suspend fun delete(searchHistoryEntity: SearchHistoryEntity)

    // Fetch all search history entities once
    @Query("SELECT * FROM search_history_entity")
    suspend fun fetchAllOnce(): List<SearchHistoryEntity>

    // Delete all search history entities
    @Query("DELETE FROM search_history_entity")
    suspend fun deleteAll()

    // Fetch all search history entities as a Flow, ordered by ID descending
    @Query("SELECT * FROM search_history_entity ORDER BY id DESC")
    fun fetchAllSearchHistoryEntities(): Flow<List<SearchHistoryEntity>>
}