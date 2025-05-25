package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String,
    val strCategory: String,
    val strArea: String,
    val strIngredient1: String?,
    val strIngredient2: String?,
    // ... include up to strIngredient20 and strMeasure1 to strMeasure20 as needed
    val strMeasure1: String?,
    val strMeasure2: String?,
    // ... other fields as needed
    val strSource: String?,
    val strYoutube: String?
) : Serializable