package ru.netology.nmedia.di

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.data.auth.AuthService
import ru.netology.nmedia.data.network.PostService
import java.util.concurrent.TimeUnit


val networkModule = module {

    single<Moshi> {
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    single<Converter.Factory>(named("converterFactory")) {
        MoshiConverterFactory.create(get())
    }

    single<Interceptor>(named("logger")) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    single<Interceptor>(named("chucker")) {
        ChuckerInterceptor.Builder(androidContext())
            .collector(ChuckerCollector(androidContext()))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    single<Interceptor>(named("auth")) {
        Interceptor { chain ->
            val request = AppAuth.getInstance().state.value?.token?.let { token ->
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            } ?: chain.request()
            chain.proceed(request)
        }
    }

    single {
        provideOkHttpClient(
            logger = get(named("logger")),
            chucker = get(named("chucker")),
            auth = get(named("auth"))
        )
    }

    single<PostService> {
        Retrofit.Builder()
            .addConverterFactory(get(named("converterFactory")))
            .client(get())
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create()
    }

    single<AuthService> {
        Retrofit.Builder()
            .addConverterFactory(get(named("converterFactory")))
            .client(get())
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create()
    }
}

private fun provideOkHttpClient(
    logger: Interceptor,
    chucker: Interceptor,
    auth: Interceptor
): OkHttpClient {
    val okHttpBuilder = OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(30L, TimeUnit.SECONDS)
        .addInterceptor(auth)

    if (BuildConfig.IS_LOGS_ENABLED) {
        okHttpBuilder.addInterceptor(logger)
        okHttpBuilder.addInterceptor(chucker)
    }

    return okHttpBuilder.build()
}
