package PGR208.exam.edamamapp.models
import java.io.Serializable

data class MealResponse(
    val meals: List<Meal>?
) : Serializable