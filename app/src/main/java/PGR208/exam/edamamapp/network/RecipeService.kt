package PGR208.exam.edamamapp.network

import PGR208.exam.edamamapp.models.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService {

    @GET("api/recipes/v2")
    fun getRecipe(
        @Query("app_key") appKey: String,
        @Query("app_id") appId: String,
        @Query("type") type: String,
        @Query("diet") diet: String?,
        @Query("mealType") mealType: String?,
        @Query("q") q: String
    ) : Call<RecipeResponse>

}