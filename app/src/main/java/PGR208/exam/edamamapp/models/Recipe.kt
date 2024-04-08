package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Recipe(
    val calories: Double,
    val cautions: List<String>,
    val co2EmissionsClass: String,
    val cuisineType: List<String>,
    val dietLabels: List<String>,
    val digest: List<Digest>,
    val dishType: List<String>,
    val externalId: String,
    val glycemicIndex: Int,
    val healthLabels: List<String>,
    val image: String,
    val images: Images,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val label: String,
    val mealType: List<String>,
    val shareAs: String,
    val source: String,
    val tags: List<String>,
    val totalCO2Emissions: Double,
    val totalDaily: TotalDaily,
    val totalNutrients: TotalNutrients,
    val totalWeight: Double,
    val totalTime: Int,
    val uri: String,
    val url: String,
    val yield: Int
): Serializable
