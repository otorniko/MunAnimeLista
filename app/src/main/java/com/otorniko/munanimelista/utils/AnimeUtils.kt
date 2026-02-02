package com.otorniko.munanimelista.utils

import com.otorniko.munanimelista.data.AnimeNode

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