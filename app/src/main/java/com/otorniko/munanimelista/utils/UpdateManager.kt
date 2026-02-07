package com.otorniko.munanimelista.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URL

suspend fun downloadApk(context: Context, url: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            // Create a temporary file in the app's cache directory
            val file = File(context.cacheDir, "update.apk")
            if (file.exists()) file.delete()
            // Open connection
            val connection = URL(url).openConnection()
            connection.connect()
            val input = connection.getInputStream()
            val output = file.outputStream()
            // Download
            input.use { input ->
                output.use { output ->
                    input.copyTo(output)
                }
            }
            file // Return the downloaded file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun installApk(context: Context, apkFile: File) {
    val packageInstaller = context.packageManager.packageInstaller
    val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
                                               )
    // 1. Create a Session
    val sessionId = packageInstaller.createSession(params)
    val session = packageInstaller.openSession(sessionId)

    try {
        // 2. Stream the APK file into the Session
        val out = session.openWrite("CO_UPDATE", 0, -1)
        val input = FileInputStream(apkFile)

        input.use { inputStream ->
            out.use { outputStream ->
                inputStream.copyTo(outputStream)
                session.fsync(outputStream) // Ensure bytes are written
            }
        }
        // 3. Create a PendingIntent for the result
        // We need a BroadcastReceiver to handle the "Install Complete" (or failed) signal
        val intent = Intent(context, InstallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                                                      )
        // 4. Commit (Start the installation)
        session.commit(pendingIntent.intentSender)

    } catch (e: IOException) {
        e.printStackTrace()
        session.abandon() // Clean up if something failed
    }
}