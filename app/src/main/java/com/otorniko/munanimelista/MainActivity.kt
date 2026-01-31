package com.otorniko.munanimelista

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.otorniko.munanimelista.data.AnimeViewModel
import com.otorniko.munanimelista.data.MalApi
import com.otorniko.munanimelista.data.MyListTab
import com.otorniko.munanimelista.data.TokenManager
import com.otorniko.munanimelista.ui.components.AnimeDetailsScreen
import com.otorniko.munanimelista.ui.components.AnimeListScreen
import com.otorniko.munanimelista.ui.components.AppDrawerContent
import com.otorniko.munanimelista.ui.components.BrowseScreen
import com.otorniko.munanimelista.ui.components.LoginScreen
import com.otorniko.munanimelista.ui.theme.MunAnimeListaTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // 1. STATE VARIABLES
            val tokenManager = remember { TokenManager(context) }
            var clientId by remember { mutableStateOf("") }
            var isError by remember { mutableStateOf(false) }
            var retryTrigger by remember { mutableIntStateOf(0) }

            var isLoggedIn by remember {
                mutableStateOf(tokenManager.getToken() != null)
            }

            LaunchedEffect(retryTrigger) {
                isError = false
                val remoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
                remoteConfig.setConfigSettingsAsync(configSettings)

                remoteConfig.fetchAndActivate()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val fetched = remoteConfig.getString("mal_client_id")
                            if (fetched.isNotBlank()) {
                                clientId = fetched
                            } else {
                                isError = true
                            }
                        } else {
                            isError = true
                            Log.e("Config", "Fetch failed", task.exception)
                        }
                    }
            }

            if (clientId.isNotEmpty() && !isLoggedIn) {
                val tempApi = remember {
                    val json = Json { ignoreUnknownKeys = true }
                    val client = OkHttpClient.Builder().build()
                    Retrofit.Builder()
                        .baseUrl("https://api.myanimelist.net/v2/")
                        .client(client)
                        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                        .build()
                        .create(MalApi::class.java)
                }

                LaunchedEffect(Unit) {
                    val data: Uri? = intent?.data
                    if (data != null && data.scheme == "munanimelista" && data.host == "auth") {
                        val code = data.getQueryParameter("code")
                        val verifier = context.getSharedPreferences("auth_prefs", 0)
                            .getString("temp_verifier", null)

                        if (code != null && verifier != null) {
                            scope.launch {
                                try {
                                    val tokenResponse = tempApi.getAccessToken(
                                        clientId = clientId,
                                        code = code,
                                        codeVerifier = verifier,
                                        redirectUri = "munanimelista://auth"
                                    )
                                    tokenManager.saveToken(
                                        token = tokenResponse.accessToken,
                                        refreshToken = tokenResponse.refreshToken
                                    )
                                    isLoggedIn = true
                                } catch (e: Exception) {
                                    Log.e("Login", "Failed", e)
                                }
                            }
                        }
                    }
                }
            }
            if (clientId.isNotEmpty()) {
                MunAnimeListaTheme {
                    if (isLoggedIn) {
                        val navController = rememberNavController()
                        val viewModel: AnimeViewModel = viewModel()
                        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                        val scope = rememberCoroutineScope()
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                AppDrawerContent(
                                    onMyListClick = { tab ->
                                        scope.launch { drawerState.close() }
                                        navController.navigate("list?status=${tab.name}") {
                                            popUpTo("list?status=ALL") { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    },
                                    onCategoryClick = { category ->
                                        scope.launch { drawerState.close() }
                                        navController.navigate("browse/${category.apiKey}/${category.label}") {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        ) {
                            NavHost(navController = navController, startDestination = "list") {
                                composable(
                                    route = "list?status={status}",
                                    arguments = listOf(navArgument("status") {
                                        defaultValue = "ALL"
                                        type = NavType.StringType
                                    })
                                ) { entry ->
                                    val statusStr = entry.arguments?.getString("status") ?: "ALL"
                                    val tab = try {
                                        MyListTab.valueOf(statusStr)
                                    } catch (e: Exception) {
                                        MyListTab.ALL
                                    }

                                    AnimeListScreen(
                                        viewModel = viewModel,
                                        initialTab = tab,
                                        onAnimeClick = { id -> navController.navigate("details/$id") },
                                        onOpenDrawer = { scope.launch { drawerState.open() } }
                                    )
                                }
                                composable(
                                    route = "details/{animeId}",
                                    arguments = listOf(navArgument("animeId") {
                                        type = NavType.IntType
                                    })
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("animeId") ?: 0
                                    AnimeDetailsScreen(
                                        animeId = id,
                                        onBackClick = { navController.popBackStack() },
                                        onAnimeClick = { newId -> navController.navigate("details/$newId") },
                                        onStatusChanged = {
                                            viewModel.refresh()
                                        }
                                    )
                                }
                                composable(
                                    route = "browse/{categoryType}/{categoryTitle}",
                                    arguments = listOf(
                                        navArgument("categoryType") { type = NavType.StringType },
                                        navArgument("categoryTitle") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val type =
                                        backStackEntry.arguments?.getString("categoryType") ?: "all"
                                    val title =
                                        backStackEntry.arguments?.getString("categoryTitle")
                                            ?: "Browse"
                                    LaunchedEffect(type) {
                                        viewModel.initBrowse(type)
                                    }
                                    BrowseScreen(
                                        title = title,
                                        viewModel = viewModel,
                                        onOpenDrawer = { scope.launch { drawerState.open() } },
                                        onAnimeClick = { id -> navController.navigate("details/$id") }
                                    )
                                }
                            }
                        }
                    } else {
                        LoginScreen(clientId = clientId)
                    }
                }
            } else if (isError) {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Could not connect to server.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { retryTrigger++ }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
