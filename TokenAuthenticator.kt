package com.example.network

import com.example.network.AuthApi
import com.example.network.AuthConstants
import com.example.network.AuthTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val api: AuthApi,
    private val tokenRepository: AuthTokenRepository,
) : Authenticator {
    // Called when server requests authorization, i.e. it returned HTTP 401. This happens if the current access token
    // has expired. In this case, refresh the token and send the original request with the fresh token.
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            tokenRepository.getAuthToken()?.refreshToken?.let { safeRefreshToken ->
                api.refreshToken(safeRefreshToken).let { freshToken ->
                    tokenRepository.storeAuthToken(freshToken)
                    response.request.newBuilder()
                        .authorizationHeader(freshToken.accessToken)
                        .build()
                }
            } ?: kotlin.run {
                // token refresh failed, consider logging user out
                null
            }
        }
    }
    
    private fun Request.Builder.authorizationHeader(accessToken: String): Request.Builder {
        return header(
            AuthConstants.AUTHORIZATION_HEADER,
            "${AuthConstants.BEARER} $accessToken"
        )
    }
}

