package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Links(
    val next: Next,
    val self: Self
): Serializable
