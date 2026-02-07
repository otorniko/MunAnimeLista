package com.otorniko.munanimelista.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun isFutureDate(dateStr: String?): Boolean {
    if (dateStr.isNullOrEmpty()) return false
    val formats = listOf(
            "yyyy-MM-dd", // e.g. "2025-05-14"
            "yyyy-MM",    // e.g. "2025-05"
            "yyyy"        // e.g. "2025"
                        )
    val now = Date()

    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false
            val date = sdf.parse(dateStr)
            if (date != null) {
                return date.after(now)
            }
        } catch (e: Exception) {
            continue
        }
    }
    return false
}