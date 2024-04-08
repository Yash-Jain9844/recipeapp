package PGR208.exam.edamamapp.models

import java.io.Serializable

data class Digest (
    val daily: Double,
    val hasRDI: Boolean,
    val label: String,
    val schemaOrgTag: String,
    val sub: List<Sub>,
    val tag: String,
    val total: Double,
    val unit: String
): Serializable