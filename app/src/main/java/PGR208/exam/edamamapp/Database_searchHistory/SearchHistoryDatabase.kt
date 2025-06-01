package PGR208.exam.edamamapp.Database_searchHistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database for storing search history
@Database(entities = [SearchHistoryEntity::class], version=3)
abstract class SearchHistoryDatabase: RoomDatabase() {

    // Provide access to SearchHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    // Companion object for singleton instance
    companion object {
        @Volatile
        private var INSTANCE: SearchHistoryDatabase? = null

        // Get or create a database instance
        fun getInstance(context: Context): SearchHistoryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // Build database with destructive migration
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SearchHistoryDatabase::class.java,
                        "search_history_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}