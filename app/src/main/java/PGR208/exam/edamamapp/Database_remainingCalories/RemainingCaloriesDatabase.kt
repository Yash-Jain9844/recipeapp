package PGR208.exam.edamamapp.Database_remainingCalories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RemainingCaloriesEntity::class], version = 2)
abstract class RemainingCaloriesDatabase: RoomDatabase() {

    abstract fun remainingCaloriesDao(): RemainingCaloriesDao

    companion object{

        @Volatile
        private var INSTANCE: RemainingCaloriesDatabase? = null

        fun getInstance(context: Context): RemainingCaloriesDatabase{

            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RemainingCaloriesDatabase::class.java,
                        "remaining_calories_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}