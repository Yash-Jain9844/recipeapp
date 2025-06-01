package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.models.Meal

// Singleton object to store the list of meals globally
object MealList {
    // Mutable list to hold meal search results
    val mealsList = mutableListOf<Meal>()
}