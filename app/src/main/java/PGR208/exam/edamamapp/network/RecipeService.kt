package PGR208.exam.edamamapp.network

import PGR208.exam.edamamapp.models.MealResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {
    @GET("search.php")
    fun searchMeals(@Query("s") query: String): Call<MealResponse>
}