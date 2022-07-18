package com.example.network

import retrofit2.http.POST

interface AuthApi {
    @POST("/login")
    suspend fun login(): AuthTokenEntity
    
    @POST("/refreshToken")
    suspend fun refreshToken(refreshToken: String): AuthTokenEntity
}

