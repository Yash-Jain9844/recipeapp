package PGR208.exam.edamamapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import PGR208.exam.edamamapp.Database_Favorites.FavoritesDatabase
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryDatabase
import PGR208.exam.edamamapp.Database_settings.SettingsDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DatabaseApp : Application() {

    lateinit var dbFavorites: FavoritesDatabase
    lateinit var dbSearchHistory: SearchHistoryDatabase
    lateinit var dbSettings: SettingsDatabase

    override fun onCreate() {
        super.onCreate()

        dbFavorites = FavoritesDatabase.getInstance(this)
        dbSearchHistory = Room.databaseBuilder(
            this,
            SearchHistoryDatabase::class.java,
            "search_history_database"
        ).build()

        dbSettings = Room.databaseBuilder(
            this,
            SettingsDatabase::class.java,
            "settings_database"
        ).build()

        // Apply dark theme by default if no preference is set
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