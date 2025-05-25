package PGR208.exam.edamamapp

import android.app.Application
import PGR208.exam.edamamapp.Database_Favorites.FavoritesDatabase
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryDatabase
import PGR208.exam.edamamapp.Database_settings.SettingsDatabase

class DatabaseApp : Application() {
    val dbSettings by lazy { SettingsDatabase.getInstance(this) }
    val dbFavorites by lazy { FavoritesDatabase.getInstance(this) }
    val dbSearchHistory by lazy { SearchHistoryDatabase.getInstance(this) }
}