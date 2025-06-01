package PGR208.exam.edamamapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import PGR208.exam.edamamapp.Database_Favorites.FavoritesDatabase
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryDatabase
import PGR208.exam.edamamapp.Database_settings.SettingsDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Application class to initialize Room databases and apply theme preferences
class DatabaseApp : Application() {
    // Database instances for favorites, search history, and settings
    lateinit var dbFavorites: FavoritesDatabase
    lateinit var dbSearchHistory: SearchHistoryDatabase
    lateinit var dbSettings: SettingsDatabase

    // Called when the application is created
    override fun onCreate() {
        super.onCreate()

        // Initialize favorites database
        dbFavorites = FavoritesDatabase.getInstance(this)
        // Initialize search history database
        dbSearchHistory = Room.databaseBuilder(
            this,
            SearchHistoryDatabase::class.java,
            "search_history_database"
        ).build()
        // Initialize settings database
        dbSettings = Room.databaseBuilder(
            this,
            SettingsDatabase::class.java,
            "settings_database"
        ).build()

        // Apply theme preference from settings (default to dark if none set)
        GlobalScope.launch {
            val themePreference = dbSettings.settingsDao().fetchThemePreference() ?: "dark"
            when (themePreference) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Default to dark
            }
        }
    }
}