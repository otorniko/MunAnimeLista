package com.otorniko.munanimelista.ui.components

import com.otorniko.munanimelista.data.MyListTab
import com.otorniko.munanimelista.data.RankingCategory

sealed class Screen(val route: String) {
    object AnimeList : Screen("anime_list?status={status}") {
        fun createRoute(tab: MyListTab): String {
            return "anime_list?status=${tab.name}"
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
            return "browse/${category.apiKey}/${category.label}"
        }
    }
}