package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity

// Singleton object to store the list of search history entities globally
object SearchHistoryEntityList {
    // Mutable list to hold search history entities
    var searchHistoryEntityList = mutableListOf<SearchHistoryEntity>()
}