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

class SearchHistoryActivity : AppCompatActivity() {
    private var binding: ActivitySearchHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao()
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        binding?.rvRecipe?.layoutManager = LinearLayoutManager(this)

        val adapter = SearchHistoryAdapter(
            context = this@SearchHistoryActivity,
            favoritesDao = favoritesDao,
            settingsDao = settingsDao
        )
        binding?.rvRecipe?.adapter = adapter

        // Observe history
        lifecycleScope.launch {
            searchHistoryDao.fetchAllSearchHistoryEntities().collect { historyList ->
                adapter.updateData(historyList)
            }
        }

        // Clear button logic
        binding?.btnClearHistory?.setOnClickListener {
            lifecycleScope.launch {
                searchHistoryDao.deleteAll()
            }
        }
    }
}
