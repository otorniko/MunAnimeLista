package com.otorniko.munanimelista.data

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenManager.getToken()
        if (token == null) {
            return chain.proceed(original)
        }
        val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        return chain.proceed(request)
    }
}