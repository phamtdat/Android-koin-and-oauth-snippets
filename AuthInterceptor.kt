package com.example.network

import cz.example.network.AuthConstants
import com.example.network.AuthTokenRepository
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attach authorization header with access token to network requests that don't yet have this header.
 */
class AuthInterceptor(
    private val tokenRepository: AuthTokenRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        if (request.header(AuthConstants.AUTHORIZATION_HEADER) == null) {
            tokenRepository.getAuthToken()?.let { token ->
                builder.addHeader(
                    AuthConstants.AUTHORIZATION_HEADER,
                    "${AuthConstants.BEARER} ${token.accessToken}"
                )
            }
        }
        return chain.proceed(builder.build())
    }
}

