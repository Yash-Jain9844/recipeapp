package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.DatabaseApp
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import PGR208.exam.edamamapp.databinding.ActivitySearchHistoryBinding
import android.util.Log
import kotlinx.coroutines.launch

// Activity for displaying and managing search history
class SearchHistoryActivity : AppCompatActivity() {
    // View binding for the search history layout
    private var binding: ActivitySearchHistoryBinding? = null

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Access DAOs for favorites, settings, and search history
        val favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao()
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        // Set up RecyclerView with a linear layout manager
        binding?.rvRecipe?.layoutManager = LinearLayoutManager(this)

        // Initialize the SearchHistoryAdapter
        val adapter = SearchHistoryAdapter(
            context = this@SearchHistoryActivity,
            favoritesDao = favoritesDao,
            settingsDao = settingsDao
        )
        binding?.rvRecipe?.adapter = adapter

        // Observe and display search history
        lifecycleScope.launch {
            searchHistoryDao.fetchAllSearchHistoryEntities().collect { historyList ->
                adapter.updateData(historyList)
            }
        }

        // Set up clear history button to delete all search history
        binding?.btnClearHistory?.setOnClickListener {
            lifecycleScope.launch {
                searchHistoryDao.deleteAll()
            }
        }
    }
}