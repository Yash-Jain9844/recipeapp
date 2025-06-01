package PGR208.exam.edamamapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

// MainActivity serves as the main entry point of the app, handling recipe search and navigation
class MainActivity : AppCompatActivity() {
    // View binding for the main activity layout
    private var binding: ActivityMainBinding? = null
    // Button references for search, settings, search history, and favorites
    private var searchBtn: Button? = null
    private var settingsBtn: Button? = null
    private var searchHistoryBtn: Button? = null
    private var favoritesBtn: Button? = null
    // Stores the maximum number of search history items from settings
    private var maxSearchHistoryItems: Int = 0

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the main activity layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Set up RecyclerView with a linear layout manager for displaying recipes
        binding?.rvRecipe?.layoutManager = LinearLayoutManager(this)

        // Access the search history DAO from the application database
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
        // Clear search history on app start (optional, for fresh start)
        lifecycleScope.launch {
            searchHistoryDao.deleteAll()
        }

        // Access settings DAO to retrieve or set default settings
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        lifecycleScope.launch {
            // If no settings exist, insert default settings
            if (settingsDao.getRowCount() == 0) {
                settingsDao.insert(
                    SettingsEntity(
                        desiredDiet = "Cheese", // Default diet preference
                        mealPriority = "None",  // Default meal priority
                        maxSearchHistoryItems = 10 // Default max search history items
                    )
                )
            }
            // Fetch the maximum search history items from settings
            maxSearchHistoryItems = settingsDao.fetchMaxSearchHistoryItems().firstOrNull() ?: 10
        }

        // Check if internet permission is granted before setting up UI
        if (isPermissionGranted()) {
            setupUI()
        } else {
            requestInternetPermission()
        }
    }

    // Inflate the options menu (for generating recipes)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Handle menu item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Navigate to GenerateRecipeActivity when the generate recipe menu item is clicked
            R.id.action_generate_recipe -> {
                startActivity(Intent(this, GenerateRecipeActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Clean up resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    // Set up UI elements and their listeners
    private fun setupUI() {
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        // Perform an initial search with a default query
        searchMeals("cheese")

        // Set up search button click listener
        searchBtn = binding?.btnSearch
        searchBtn?.setOnClickListener {
            lifecycleScope.launch {
                // Get the user's search input
                val inputQuery = binding?.tvSearchInput?.text.toString().trim()

                // Use default diet from settings if input is empty
                val finalQuery = if (inputQuery.isEmpty()) {
                    settingsDao.fetchDesiredDietOnce() ?: "cheese"
                } else {
                    inputQuery
                }

                // Validate query and perform search
                if (finalQuery.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Please enter a search query", Toast.LENGTH_SHORT).show()
                } else {
                    searchMeals(finalQuery)
                }
            }
        }

        // Set up settings button to navigate to SettingsActivity
        settingsBtn = binding?.btnSettings
        settingsBtn?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Set up search history button to navigate to SearchHistoryActivity
        searchHistoryBtn = binding?.btnSearchHistory
        searchHistoryBtn?.setOnClickListener {
            startActivity(Intent(this, SearchHistoryActivity::class.java))
        }

        // Set up favorites button to navigate to FavoritesActivity
        favoritesBtn = binding?.btnFavorites
        favoritesBtn?.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    // Check if internet permission is granted
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }

    // Request internet permission from the user
    private fun requestInternetPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET),
            Constants.INTERNET_PERMISSION_REQUEST_CODE
        )
    }

    // Perform a search for meals using the provided query
    private fun searchMeals(query: String, retryCount: Int = 0) {
        // Check for network availability
        if (!Constants.isNetworkAvailable(this)) {
            Log.e("MainActivity", "No internet connection")
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.visibility = View.GONE
            return
        }

        // Show progress bar during API call
        Log.d("MainActivity", "Starting search for: $query, showing ProgressBar")
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.rvRecipe?.adapter = null

        // Set up Retrofit for API call
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create RecipeService instance
        val service = retrofit.create(RecipeService::class.java)
        val call = service.searchMeals(query)

        // Enqueue the API call
        call.enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                lifecycleScope.launch {
                    // Delay to ensure smooth UI update
                    delay(500)
                    binding?.progressBar?.visibility = View.GONE
                    Log.d("MainActivity", "API response received, hiding ProgressBar")

                    // Handle successful response
                    if (response.isSuccessful) {
                        val meals = response.body()?.meals
                        Log.d("MainActivity", "API response: Success, meals count: ${meals?.size ?: 0}")
                        if (!meals.isNullOrEmpty()) {
                            // Clear and update the meals list
                            MealList.mealsList.clear()
                            MealList.mealsList.addAll(meals)

                            // Save search results to history
                            lifecycleScope.launch {
                                val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
                                val maxItems = minOf(maxSearchHistoryItems, meals.size)

                                // Insert search results into history
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

                                // Trim history if it exceeds max items
                                val updatedEntries = searchHistoryDao.fetchAllOnce()
                                if (updatedEntries.size > maxSearchHistoryItems) {
                                    val toDelete = updatedEntries.take(updatedEntries.size - maxSearchHistoryItems)
                                    for (entry in toDelete) {
                                        searchHistoryDao.delete(entry)
                                    }
                                }
                            }

                            // Set up RecyclerView adapter with search results
                            val adapter = MainAdapter(
                                MealList.mealsList,
                                this@MainActivity,
                                (application as DatabaseApp).dbFavorites.favoritesDao(),
                                (application as DatabaseApp).dbSettings.settingsDao()
                            )
                            binding?.rvRecipe?.adapter = adapter
                        } else {
                            // Retry with fallback query if no results are found
                            if (retryCount == 0) {
                                Log.w("MainActivity", "No meals found for query: $query, retrying with 'salad'")
                                searchMeals("salad", retryCount + 1)
                            } else {
                                Log.w("MainActivity", "No meals found for query: $query")
                                Toast.makeText(this@MainActivity, "No meals found for $query", Toast.LENGTH_SHORT).show()
                                binding?.rvRecipe?.adapter = null
                            }
                        }
                    } else {
                        // Handle API error
                        Log.e("MainActivity", "API error: Code ${response.code()}, Message: ${response.message()}")
                        Toast.makeText(this@MainActivity, "API error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        binding?.rvRecipe?.adapter = null
                    }
                }
            }

            // Handle API call failure
            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                lifecycleScope.launch {
                    delay(500)
                    binding?.progressBar?.visibility = View.GONE
                    Log.d("MainActivity", "API failure, hiding ProgressBar")
                    Log.e("MainActivity", "API failure: ${t.message ?: "Unknown error"}")
                    Toast.makeText(this@MainActivity, "Failed to load meals: ${t.message}", Toast.LENGTH_SHORT).show()
                    binding?.rvRecipe?.adapter = null
                }
            }
        })
    }

    // Handle permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set up UI
                setupUI()
            } else {
                // Permission denied, show error
                Log.e("MainActivity", "Internet permission denied")
                Toast.makeText(this, "Internet permission required", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}