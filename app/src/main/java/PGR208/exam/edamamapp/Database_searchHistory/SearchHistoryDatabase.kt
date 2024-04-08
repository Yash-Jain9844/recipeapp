package PGR208.exam.edamamapp.Database_searchHistory

import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchHistoryEntity::class], version=3)
abstract class SearchHistoryDatabase: RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object{

        @Volatile
        private var INSTANCE: SearchHistoryDatabase? = null

        fun getInstance(context: Context): SearchHistoryDatabase{

            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
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