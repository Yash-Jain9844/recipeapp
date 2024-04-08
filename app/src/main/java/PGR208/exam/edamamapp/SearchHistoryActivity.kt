package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import PGR208.exam.edamamapp.databinding.ActivitySearchHistoryBinding
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchHistoryActivity : AppCompatActivity() {

    private var binding: ActivitySearchHistoryBinding? = null
    private lateinit var mSharedPreferences: SharedPreferences
    private var flowList: MutableList<SearchHistoryEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Setting up binding */
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao()
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        /** Fetching Search History Database records into RecyclerView */
        lifecycleScope.launch{
            searchHistoryDao.fetchAllSearchHistoryEntities().collect {
                SearchHistoryEntityList.searchHistoryEntityList = it as MutableList<SearchHistoryEntity>
                Log.e("SHList ", "${SearchHistoryEntityList.searchHistoryEntityList}")
                val adapter = SearchHistoryAdapter(SearchHistoryEntityList.searchHistoryEntityList,
                    this@SearchHistoryActivity,
                    favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao(),
                    settingsDao = (application as DatabaseApp).dbSettings.settingsDao(),
                    remainingCaloriesDao = (application as DatabaseApp).dbRemainingCalories.remainingCaloriesDao()
                )
                binding?.rvRecipe?.adapter = adapter
            }
        }
    }

}