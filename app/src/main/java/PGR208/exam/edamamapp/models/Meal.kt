package PGR208.exam.edamamapp.models

import java.io.Serializable

// Data class representing a meal from TheMealDB API
data class Meal(
    val idMeal: String, // Unique ID of the meal
    val strMeal: String, // Meal name
    val strMealThumb: String, // Thumbnail image URL
    val strInstructions: String, // Cooking instructions
    val strCategory: String, // Meal category
    val strArea: String, // Cultural origin
    val strIngredient1: String?, // Ingredient 1
    val strIngredient2: String?, // Ingredient 2
    // Additional ingredients (up to 20) can be added as needed
    val strMeasure1: String?, // Measurement for ingredient 1
    val strMeasure2: String?, // Measurement for ingredient 2
    // Additional measurements can be added as needed
    val strSource: String?, // Source URL of the recipe
    val strYoutube: String? // YouTube video URL
) : Serializable