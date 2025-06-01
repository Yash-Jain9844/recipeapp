package PGR208.exam.edamamapp.models
import java.io.Serializable

// Data class representing the API response containing a list of meals
data class MealResponse(
    val meals: List<Meal>? // List of meals, nullable if no results
) : Serializable