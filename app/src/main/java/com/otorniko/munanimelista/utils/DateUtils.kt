package com.otorniko.munanimelista.utils

fun getSeasonString(dateStr: String?): String? {
    if (dateStr.isNullOrEmpty()) return null
    return try {
        val parts = dateStr.split("-")
        val year = parts[0]
        val month = if (parts.size > 1) parts[1].toInt() else 1
        val season = when (month) {
            1, 2, 3 -> "Winter"
            4, 5, 6 -> "Spring"
            7, 8, 9 -> "Summer"
            10, 11, 12 -> "Fall"
            else -> ""
        }
        "$season $year"
    } catch (e: Exception) {
        null
    }
}