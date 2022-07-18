package com.example.network

/**
 * Interface that provides the locally stored (either DB or SharedPreferences) access (and refresh) token. Provide your own implementation. 
*/
interface AuthTokenRepository {
    fun getAuthToken(): AuthTokenEntity?
    fun storeAuthToken(token: AuthTokenEntity?)
}

