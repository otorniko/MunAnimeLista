package com.otorniko.munanimelista.ui.screens

import com.otorniko.munanimelista.data.model.ListStatus
import com.otorniko.munanimelista.data.model.RankingCategory

sealed class Screen(val route: String) {
    object AnimeList : Screen("anime_list?status={status}") {
        fun createRoute(tab: ListStatus?): String {
            val statusStr = tab?.name ?: "ALL"
            return "anime_list?status=$statusStr"
        }
    }

    object Settings : Screen("settings")
    object AnimeDetails : Screen("anime_details/{id}?origin={origin}") {
        fun createRoute(id: Int, origin: String): String {
            return "anime_details/$id?origin=$origin"
        }
    }

    object Browse : Screen("browse/{categoryType}/{categoryTitle}") {
        fun createRoute(category: RankingCategory): String {
            return "browse/${category.name}/${category.label}"
        }
    }
}