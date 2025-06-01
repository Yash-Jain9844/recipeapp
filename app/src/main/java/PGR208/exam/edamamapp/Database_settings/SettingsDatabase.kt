package PGR208.exam.edamamapp.Database_settings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Room database for storing app settings
@Database(entities = [SettingsEntity::class], version = 2)
abstract class SettingsDatabase : RoomDatabase() {

    // Provide access to SettingsDao
    abstract fun settingsDao(): SettingsDao

    // Companion object for singleton instance
    companion object {
        @Volatile
        private var INSTANCE: SettingsDatabase? = null

        // Get or create a database instance
        fun getInstance(context: Context): SettingsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    // Build database with migration support
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SettingsDatabase::class.java,
                        "settings_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // Migration from version 1 to 2, adding themePreference column
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    ALTER TABLE settings_entity
                    ADD COLUMN themePreference TEXT NOT NULL DEFAULT 'system'
                """)
            }
        }
    }
}