package PGR208.exam.edamamapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import PGR208.exam.edamamapp.databinding.ActivityMainBinding
import PGR208.exam.edamamapp.models.MealResponse
import PGR208.exam.edamamapp.network.RecipeService
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import kotlinx.coroutines.flow.firstOrNull

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var searchBtn: Button? = null
    private var settingsBtn: Button? = null
    private var searchHistoryBtn: Button? = null
    private var favoritesBtn: Button? = null
    private var maxSearchHistoryItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Initialize RecyclerView
        binding?.rvRecipe?.layoutManager = LinearLayoutManager(this)

        // Clear search history on app start
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
        lifecycleScope.launch {
            searchHistoryDao.deleteAll()
        }

        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        lifecycleScope.launch {
            if (settingsDao.getRowCount() == 0) {
                settingsDao.insert(
                    SettingsEntity(
                        desiredDiet = "Cheese", // Default to Cheese
                        mealPriority = "None",
                        maxSearchHistoryItems = 10
                    )
                )
            }
            maxSearchHistoryItems = settingsDao.fetchMaxSearchHistoryItems().firstOrNull() ?: 10
        }

        if (isPermissionGranted()) {
            setupUI()
        } else {
            requestInternetPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupUI() {
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        // Load meals automatically with a default query
        searchMeals("cheese") // Reliable vegetarian query with many results

        searchBtn = binding?.btnSearch
        searchBtn?.setOnClickListener {
            lifecycleScope.launch {
                val inputQuery = binding?.tvSearchInput?.text.toString().trim()

                val finalQuery = if (inputQuery.isEmpty()) {
                    settingsDao.fetchDesiredDietOnce() ?: "cheese"
                } else {
                    inputQuery
                }

                if (finalQuery.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Please enter a search query", Toast.LENGTH_SHORT).show()
                } else {
                    searchMeals(finalQuery)
                }
            }
        }

        settingsBtn = binding?.btnSettings
        settingsBtn?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        searchHistoryBtn = binding?.btnSearchHistory
        searchHistoryBtn?.setOnClickListener {
            startActivity(Intent(this, SearchHistoryActivity::class.java))
        }

        favoritesBtn = binding?.btnFavorites
        favoritesBtn?.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInternetPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            Constants.INTERNET_PERMISSION_REQUEST_CODE
        )
    }

    private fun searchMeals(query: String, retryCount: Int = 0) {
        if (!Constants.isNetworkAvailable(this)) {
            Log.e("MainActivity", "No internet connection")
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.visibility = View.GONE
            return
        }

        Log.d("MainActivity", "Starting search for: $query, showing ProgressBar")

        // Show loader and clear RecyclerView
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.rvRecipe?.adapter = null

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RecipeService::class.java)
        val call = service.searchMeals(query)

        call.enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                lifecycleScope.launch {
                    // Ensure ProgressBar is visible for at least 500ms
                    delay(500)

                    // Hide loader
                    binding?.progressBar?.visibility = View.GONE
                    Log.d("MainActivity", "API response received, hiding ProgressBar")

                    if (response.isSuccessful) {
                        val meals = response.body()?.meals
                        Log.d("MainActivity", "API response: Success, meals count: ${meals?.size ?: 0}")
                        if (!meals.isNullOrEmpty()) {
                            MealList.mealsList.clear()
                            MealList.mealsList.addAll(meals)

                            lifecycleScope.launch {
                                val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
                                val maxItems = minOf(maxSearchHistoryItems, meals.size)

                                for (i in 0 until maxItems) {
                                    val meal = meals[i]
                                    val entity = SearchHistoryEntity(
                                        image = meal.strMealThumb ?: "",
                                        label = meal.strMeal,
                                        dietLabel = meal.strCategory ?: "",
                                        healthLabel = "",
                                        mealType = meal.strCategory ?: "",
                                        url = meal.strSource ?: ""
                                    )
                                    searchHistoryDao.insert(entity)
                                }

                                val updatedEntries = searchHistoryDao.fetchAllOnce()
                                if (updatedEntries.size > maxSearchHistoryItems) {
                                    val toDelete = updatedEntries.take(updatedEntries.size - maxSearchHistoryItems)
                                    for (entry in toDelete) {
                                        searchHistoryDao.delete(entry)
                                    }
                                }
                            }

                            val adapter = MainAdapter(
                                MealList.mealsList,
                                this@MainActivity,
                                (application as DatabaseApp).dbFavorites.favoritesDao(),
                                (application as DatabaseApp).dbSettings.settingsDao()
                            )
                            binding?.rvRecipe?.adapter = adapter
                        } else {
                            // Retry with fallback query if no meals found
                            if (retryCount == 0) {
                                Log.w("MainActivity", "No meals found for query: $query, retrying with 'salad'")
                                searchMeals("salad", retryCount + 1)
                            } else {
                                Log.w("MainActivity", "No meals found for query: $query")
                                Toast.makeText(this@MainActivity, "No meals found for $query", Toast.LENGTH_SHORT).show()
                                binding?.rvRecipe?.adapter = null // Ensure RecyclerView remains empty
                            }
                        }
                    } else {
                        Log.e("MainActivity", "API error: Code ${response.code()}, Message: ${response.message()}")
                        Toast.makeText(this@MainActivity, "API error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        binding?.rvRecipe?.adapter = null // Ensure RecyclerView remains empty
                    }
                }
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                lifecycleScope.launch {
                    // Ensure ProgressBar is visible for at least 500ms
                    delay(500)

                    // Hide loader
                    binding?.progressBar?.visibility = View.GONE
                    Log.d("MainActivity", "API failure, hiding ProgressBar")

                    Log.e("MainActivity", "API failure: ${t.message ?: "Unknown error"}")
                    Toast.makeText(this@MainActivity, "Failed to load meals: ${t.message}", Toast.LENGTH_SHORT).show()
                    binding?.rvRecipe?.adapter = null // Ensure RecyclerView remains empty
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupUI()
            } else {
                Log.e("MainActivity", "Internet permission denied")
                Toast.makeText(this, "Internet permission required", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}