package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Constants.INTERNET_PERMISSION_REQUEST_CODE
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesEntity
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import PGR208.exam.edamamapp.databinding.ActivityMainBinding
import PGR208.exam.edamamapp.models.RecipeResponse
import PGR208.exam.edamamapp.network.RecipeService
import android.annotation.SuppressLint
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var searchInput: String = ""
    private var searchBtn: Button? = null
    private var diet: String = ""
    private var mealType: String = ""
    private var maxCalories: Int = 0
    private var maxSearchHistoryItems: Int = 0
    private var settingsBtn: Button? = null
    private var searchHistoryBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /** Setting up binding for Main Activity */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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
        /** Setting up access to databases and applying values from the Settings database to settings variables */
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val remainingCaloriesDao = (application as DatabaseApp).dbRemainingCalories.remainingCaloriesDao()

        //TODO 1: Add the first record to the settings database on the app's first run
        // addFirstRecord(settingsDao)

        lifecycleScope.launch {
            settingsDao.fetchDesiredDiet().collect {
                if (it != null) {
                    diet = it
                }
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchMealPriority().collect {
                if (it != null) {
                    mealType = it
                }
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchCalorieIntake().collect {
                maxCalories = it
                //TODO 2: Add the first record to the remaining calories database
                /** Insertion of remaining calories into Remaining Calories Database; on the first app run */
//                remainingCaloriesDao.insert(RemainingCaloriesEntity(1, maxCalories))

                //TODO 3: Switch from insert to update
                /** Resetting remaining calories on Launch - instead of resetting on 12 AM */
                remainingCaloriesDao.update(RemainingCaloriesEntity(1, maxCalories))
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchMaxSearchHistoryItems().collect {
                maxSearchHistoryItems = it
            }
        }
        /** Connecting search button to the function that gets recipes from API */
        searchBtn = binding?.btnSearch
        searchBtn?.setOnClickListener {
            if (isPermissionGranted()) {
                RecipeResponseHitsList.recipeResponseHitsList.clear()
                searchInput = binding?.tvSearchInput?.text.toString()
                getRecipeDetails(diet, mealType, searchInput)
            } else {
                // Tutaj można wyświetlić komunikat o braku uprawnień do Internetu
                Toast.makeText(this, "Brak uprawnień do Internetu", Toast.LENGTH_SHORT).show()
            }
        }


        /** Adding intent to settings button in order to go over to Settings activity */
        settingsBtn = binding?.btnSettings
        settingsBtn?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        /** Adding intent to Search history button in order to go over to Search history activity */
        searchHistoryBtn = binding?.btnSearchHistory
        searchHistoryBtn?.setOnClickListener {
            val intent = Intent(this, SearchHistoryActivity::class.java)
            startActivity(intent)
        }
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInternetPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), INTERNET_PERMISSION_REQUEST_CODE)
    }

    /** Function to initialize a Settings database on application's first run: called from onCreate if needed */
    private fun addFirstRecord(settingsDao: SettingsDao){
        val calorieIntake = 2500
        val maxSearchHistoryItems = 10
        val desiredDiet = "balanced"
        val mealPriority = "lunch/dinner"

        lifecycleScope.launch{
            settingsDao.insert(SettingsEntity(calorieIntake = calorieIntake, maxSearchHistoryItems = maxSearchHistoryItems, desiredDiet = desiredDiet, mealPriority = mealPriority))
            Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.INTERNET_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Użytkownik udzielił uprawnienia, możesz wykonywać operacje, które wymagają dostępu do Internetu
                setupUI()
            } else {
                // Użytkownik odmówił uprawnienia, możesz obsłużyć ten przypadek w odpowiedni sposób
                // Na przykład wyświetlić komunikat o braku dostępu do Internetu
                Toast.makeText(this, "Brak uprawnień do Internetu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRecipeDetails(diet: String, mealType: String, searchInput: String) {

        if (Constants.isNetworkAvailable(this)) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: RecipeService = retrofit
                .create<RecipeService>(RecipeService::class.java)

            val listCall: Call<RecipeResponse> = service.getRecipe(
                Constants.APP_KEY, Constants.APP_ID, "public", diet, mealType, searchInput
            )

            listCall.enqueue(object : Callback<RecipeResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<RecipeResponse>,
                    response: Response<RecipeResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipeResponse: RecipeResponse? = response.body()
                        Log.i("Response Result", "$recipeResponse")
                        val searchHistoryDao = (application as DatabaseApp).dbSearchHistory.searchHistoryDao()

                        if (recipeResponse != null) {
                            RecipeResponseHitsList.recipeResponseHitsList.addAll(recipeResponse.hits)
                            if(RecipeResponseHitsList.recipeResponseHitsList.size >= maxSearchHistoryItems) {
                                SearchHistoryEntityList.searchHistoryEntityList.clear()
                                for (i in 0 until maxSearchHistoryItems) {
                                    lifecycleScope.launch {
                                        searchHistoryDao.deleteAll()
                                        searchHistoryDao.insert(
                                            SearchHistoryEntity(
                                                id = i,
                                                image = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.image,
                                                label = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.label,
                                                dietLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.dietLabels[0],
                                                healthLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.healthLabels[0],
                                                mealType = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.mealType[0],
                                                url = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.url,
                                                calories = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.calories,
                                                yield = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.yield
                                            )
                                        )
                                    }
                                    SearchHistoryEntityList.searchHistoryEntityList.add(
                                        SearchHistoryEntity(
                                            id = i,
                                            image = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.image,
                                            label = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.label,
                                            dietLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.dietLabels[0],
                                            healthLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.healthLabels[0],
                                            mealType = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.mealType[0],
                                            url = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.url,
                                            calories = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.calories,
                                            yield = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.yield
                                    )
                                    )
                                }
                            } else if(RecipeResponseHitsList.recipeResponseHitsList.size < maxSearchHistoryItems){
                                for (i in 0 until RecipeResponseHitsList.recipeResponseHitsList.size-1) {
                                    SearchHistoryEntityList.searchHistoryEntityList.removeAt(i)
                                    lifecycleScope.launch {
                                        searchHistoryDao.deleteById(i)
                                        searchHistoryDao.insert(
                                            SearchHistoryEntity(
                                                id = i,
                                                image = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.image,
                                                label = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.label,
                                                dietLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.dietLabels[0],
                                                healthLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.healthLabels[0],
                                                mealType = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.mealType[0],
                                                url = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.url,
                                                calories = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.calories,
                                                yield = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.yield
                                            )
                                        )
                                    }
                                    SearchHistoryEntityList.searchHistoryEntityList.add(
                                        SearchHistoryEntity(
                                            id = i,
                                            image = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.image,
                                            label = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.label,
                                            dietLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.dietLabels[0],
                                            healthLabel = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.healthLabels[0],
                                            mealType = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.mealType[0],
                                            url = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.url,
                                            calories = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.calories,
                                            yield = RecipeResponseHitsList.recipeResponseHitsList[i].recipe.yield
                                        )
                                    )
                                }
                            }
                        }
                        val adapter = MainAdapter(
                            RecipeResponseHitsList.recipeResponseHitsList,
                            this@MainActivity,
                            favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao(),
                            settingsDao = (application as DatabaseApp).dbSettings.settingsDao(),
                            remainingCaloriesDao = (application as DatabaseApp).dbRemainingCalories.remainingCaloriesDao()
                        )
                        binding?.rvRecipe?.adapter = adapter

                    } else {
                        val rc = response.code()
                        when (rc) {
                            400 -> {
                                Log.e("Error 400", "Bad Connection")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    Log.e("Error!", t.message.toString())
                }
            })

        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}