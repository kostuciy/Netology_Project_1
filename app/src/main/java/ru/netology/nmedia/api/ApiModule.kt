package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {
    private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): PostsApiService = retrofit.create(PostsApiService::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttp)
        .build()

    @Singleton
    @Provides
    fun provideOkHttp(appAuth: AppAuth): OkHttpClient = OkHttpClient.Builder()
//      adds header with token to every request
        .addInterceptor { chain ->
            appAuth.authState.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
//      adds logging interceptor
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            }
        ).build()
}