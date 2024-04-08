package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Nutrient(
    val uri: String,
    val label: String,
    val quantity: Double,
    val unit: String
): Serializable
