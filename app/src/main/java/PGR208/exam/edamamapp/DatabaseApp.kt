package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_Favorites.FavoritesDatabase
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesDatabase
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryDatabase
import PGR208.exam.edamamapp.Database_settings.SettingsDatabase
import android.app.Application

class DatabaseApp: Application() {
    val dbSettings by lazy{
        SettingsDatabase.getInstance(this)
    }
    val dbFavorites by lazy{
        FavoritesDatabase.getInstance(this)
    }
    val dbSearchHistory by lazy{
        SearchHistoryDatabase.getInstance(this)
    }
    val dbRemainingCalories by lazy{
        RemainingCaloriesDatabase.getInstance(this)
    }
}