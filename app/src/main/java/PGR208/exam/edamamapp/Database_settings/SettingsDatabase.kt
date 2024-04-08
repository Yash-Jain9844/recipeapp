package PGR208.exam.edamamapp.Database_settings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SettingsEntity::class], version = 1)
abstract class SettingsDatabase: RoomDatabase() {

    abstract fun settingsDao(): SettingsDao

    companion object{

        @Volatile
        private var INSTANCE: SettingsDatabase? = null

        fun getInstance(context: Context): SettingsDatabase{

            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SettingsDatabase::class.java,
                        "settings_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}