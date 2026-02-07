package com.otorniko.munanimelista.ui.screens

import android.content.Intent
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import java.security.SecureRandom

@Composable
fun LoginScreen(clientId: String) {
    val context = LocalContext.current

    fun generateVerifier(): String {
        val secureRandom = SecureRandom()
        val code = ByteArray(32)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        Text("Welcome to Mun Anime Lista", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            val verifier = generateVerifier()
            // todo
            // Ideally use a specialized storage, but SharedPrefs works for the prototype
            context.getSharedPreferences("auth_prefs", 0).edit {
                putString("temp_verifier", verifier)
            }
            // todo
            // We use 'plain' challenge method for simplicity (verifier = challenge)
            val loginUrl = "https://myanimelist.net/v1/oauth2/authorize" +
                           "?response_type=code" +
                           "&client_id=$clientId" +
                           "&code_challenge=$verifier" +
                           "&code_challenge_method=plain" +
                           "&redirect_uri=munanimelista://auth"
            val intent = Intent(Intent.ACTION_VIEW, loginUrl.toUri())
            context.startActivity(intent)
        }) {
            Text("Login with MyAnimeList")
        }
    }
}