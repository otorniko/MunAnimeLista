package com.otorniko.munanimelista.ui.components

import com.otorniko.munanimelista.data.RankingCategory

sealed class Screen(val route: String) {
    object AnimeList : Screen("anime_list") {
        fun createRoute(status: String) = "anime_list?status=$status"
    }
    object Settings : Screen("settings")
    object AnimeDetails : Screen("anime_details/{animeId}") {
        fun createRoute(id: Int) = "anime_details/$id"
    }

    object Browse : Screen("browse/{categoryType}/{categoryTitle}") {
        fun createRoute(category: RankingCategory): String {
            return "browse/${category.apiKey}/${category.label}"
        }
    }
}