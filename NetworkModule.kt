package com.example.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.synetech.base.app.AppInfo
import com.example.network.AuthApi
import com.example.network.TokenAuthenticator
import com.example.network.AuthTokenRepository
import com.example.network.AuthTokenRepositoryImpl
import com.example.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val AUTH_API_URL = "https://www.example.com/"

val networkModule = module {

    factory {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    factory { (baseUrl: String, requireAuth: Boolean) ->
        val appInfo: AppInfo = get()
        val log = appInfo.logs

        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().also {
                            it.setLevel(
                                if (log) {
                                    HttpLoggingInterceptor.Level.BODY
                                } else {
                                    HttpLoggingInterceptor.Level.NONE
                                }
                            )
                        }
                    )
                    .apply {
                        if (requireAuth) {
                            addInterceptor(get<AuthInterceptor>())
                            authenticator(get<TokenAuthenticator>())
                        }
                    }
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(baseUrl)
            .build()
    }

    single<AuthApi> {
        get<Retrofit>(parameters = { parametersOf(AUTH_API_URL, false) }).create(AuthApi::class.java)
    }

    single {
        TokenAuthenticator(
            api = get(),
            tokenRepository = get(),
        )
    }

    factory {
        AuthInterceptor(tokenRepository = get())
    }

    single<AuthTokenRepository> {
        AuthTokenRepositoryImpl()
    }
}

