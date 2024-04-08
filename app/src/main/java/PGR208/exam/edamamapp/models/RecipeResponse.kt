package PGR208.exam.edamamapp.models

import java.io.Serializable

data class RecipeResponse(
    val from: Int,
    val to: Int,
    val count: Int,
    val _links: Links,
    val hits: List<Hit>,
): Serializable
