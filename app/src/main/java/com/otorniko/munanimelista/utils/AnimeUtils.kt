package com.otorniko.munanimelista.utils

import com.otorniko.munanimelista.data.model.AnimeNode
import kotlin.math.log10
import kotlin.math.max

fun AnimeNode?.getDisplayTitles(preferEnglish: Boolean = true): Pair<String, String?> {
    if (this == null) return "" to null
    val englishTitle = this.alternativeTitles?.en?.ifBlank { null }
    val japaneseTitle = this.title
    val mainTitle = if (preferEnglish && englishTitle != null) {
        englishTitle
    } else {
        japaneseTitle
    }
    val candidateSub = if (mainTitle == englishTitle) japaneseTitle else englishTitle

    if (candidateSub == null) return mainTitle to null
    val normalizedMain = mainTitle.lowercase()
    val normalizedSub = candidateSub.lowercase()

    if (normalizedMain.contains(normalizedSub) || normalizedSub.contains(normalizedMain)) {
        return mainTitle to null
    }

    return mainTitle to candidateSub
}

fun AnimeNode?.getWeightedPopularity(): Double {
    if (this?.popularity == null || this.startSeason == null) return Double.NaN
    val sDate = this.startSeason.year
    val popRank = this.popularity
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val years = max(0, currentYear - sDate)
    return popRank * log10(years + 2.0)

}

fun AnimeNode?.getAggregateRank(): Double {
    val r = this?.rank ?: return Double.NaN
    val weightedPop = this.getWeightedPopularity()

    if (weightedPop.isNaN()) return Double.NaN
    val weightRank = 0.65
    val weightPop = 0.35

    return (weightRank * r) + (weightPop * weightedPop)
}
