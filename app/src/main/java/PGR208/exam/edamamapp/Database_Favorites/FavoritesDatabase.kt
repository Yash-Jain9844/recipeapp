package PGR208.exam.edamamapp.Database_Favorites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavoritesEntity::class], version = 2)
abstract class FavoritesDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    companion object {

        @Volatile
        private var INSTANCE: FavoritesDatabase? = null

        fun getInstance(context: Context): FavoritesDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FavoritesDatabase::class.java,
                        "favorites_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE favorites_entity
                    ADD COLUMN image TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE favorites_entity
                    ADD COLUMN dietLabel TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE favorites_entity
                    ADD COLUMN healthLabel TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE favorites_entity
                    ADD COLUMN mealType TEXT NOT NULL DEFAULT ''
                """)
                database.execSQL("""
                    ALTER TABLE favorites_entity
                    ADD COLUMN url TEXT NOT NULL DEFAULT ''
                """)
            }
        }
    }
}