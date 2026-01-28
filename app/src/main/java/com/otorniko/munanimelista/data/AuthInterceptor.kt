package com.otorniko.munanimelista.data

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val myHardcodedToken = "TODO"
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $myHardcodedToken")
            .build()
        return chain.proceed(request)
    }
}