package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Images(
    val LARGE: Image,
    val REGULAR: Image,
    val SMALL: Image,
    val THUMBNAIL: Image
): Serializable