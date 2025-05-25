package PGR208.exam.edamamapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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

        // Clear search history on app start
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
        lifecycleScope.launch {
            searchHistoryDao.deleteAll()
        }

        if (isPermissionGranted()) {
            setupUI()
        } else {
            requestInternetPermission()
        }

        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        lifecycleScope.launch {
            if (settingsDao.getRowCount() == 0) {
                settingsDao.insert(
                    SettingsEntity(
                        desiredDiet = "None",
                        mealPriority = "None",
                        maxSearchHistoryItems = 10
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupUI() {
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

        lifecycleScope.launch {
            maxSearchHistoryItems = settingsDao.fetchMaxSearchHistoryItems().firstOrNull() ?: 10
        }

        searchBtn = binding?.btnSearch
        searchBtn?.setOnClickListener {
            lifecycleScope.launch {
                val inputQuery = binding?.tvSearchInput?.text.toString().trim()

                val finalQuery = if (inputQuery.isEmpty()) {
                    val diet = settingsDao.fetchDesiredDietOnce() ?: ""
                    val meal = settingsDao.fetchMealPriorityOnce() ?: ""
                    "$diet $meal".trim()
                } else {
                    inputQuery
                }

                if (finalQuery.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No input or preferences", Toast.LENGTH_SHORT).show()
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

    private fun searchMeals(query: String) {
        if (!Constants.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RecipeService::class.java)
        val call = service.searchMeals(query)

        call.enqueue(object : Callback<MealResponse> {
            override fun onResponse(call: Call<MealResponse>, response: Response<MealResponse>) {
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    if (!meals.isNullOrEmpty()) {
                        MealList.mealsList.clear()
                        MealList.mealsList.addAll(meals)

                        lifecycleScope.launch {
                            val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()
                            val maxItems = minOf(maxSearchHistoryItems, meals.size)

                            for (i in 0 until maxItems) {
                                val meal = meals[i]
                                val entity = SearchHistoryEntity(
                                    image = meal.strMealThumb,
                                    label = meal.strMeal,
                                    dietLabel = meal.strCategory,
                                    healthLabel = "",
                                    mealType = meal.strCategory,
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
                        Toast.makeText(this@MainActivity, "No meals found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "Code: ${response.code()} Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MealResponse>, t: Throwable) {
                Log.e("API_FAILURE", t.message ?: "Unknown error")
                Toast.makeText(this@MainActivity, "API failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Constants.INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupUI()
            } else {
                Toast.makeText(this, "No internet permission", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}