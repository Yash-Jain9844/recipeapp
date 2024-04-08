package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Sub (
    val label: String,
    val tag: String,
    val schemaOrgTag: String,
    val total: Double,
    val hasDRI: Boolean,
    val daily: Double,
    val unit: String
        ): Serializable