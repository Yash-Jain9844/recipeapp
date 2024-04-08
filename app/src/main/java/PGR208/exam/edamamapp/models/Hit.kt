package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Hit(
    val recipe: Recipe,
    val _links: Links
): Serializable
