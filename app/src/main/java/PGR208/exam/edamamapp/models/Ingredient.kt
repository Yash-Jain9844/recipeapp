package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Ingredient(
    val food: String, //TODO: Food
    val foodId: String,
    val measure: String, //TODO: Measure
    val quantity: Float,
    val text: String,
    val weight: Float,
    val foodCategory: String
): Serializable
