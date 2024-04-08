package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Image( //TODO?: Constants THUMBNAIL, SMALL, REGULAR, LARGE
    val height: Int,
    val url: String,
    val width: Int
): Serializable
