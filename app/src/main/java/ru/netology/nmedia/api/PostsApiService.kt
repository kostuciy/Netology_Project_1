package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
//
//private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//
//private val logging = HttpLoggingInterceptor().apply {
//    if (BuildConfig.DEBUG) {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//}
//
//private val okhttp = OkHttpClient.Builder()
////    adds header with token to every request
//    .addInterceptor { chain ->
//        AppAuth.getInstance().authState.value.token?.let { token ->
//            val newRequest = chain.request().newBuilder()
//                .addHeader("Authorization", token)
//                .build()
//            return@addInterceptor chain.proceed(newRequest)
//        }
//        chain.proceed(chain.request())
//    }
//    .addInterceptor(logging)
//    .build()
//
//private val retrofit = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .client(okhttp)
//    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

//    returns list of new posts, which were created after post with input id
    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part file: MultipartBody.Part): Response<Media>

//    @GET("$BASE_URL/media/{url}")
//    suspend fun getImageAttachment(@Path("url") url: String): Response<URI> TODO: remove

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authenticate(
        @Field("login") login: String,
        @Field("pass") password: String
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun register(
        @Field("login") login: String,
        @Field("pass") password: String,
        @Field("name") name: String
    ): Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithAvatar(
        @Part("login") login: RequestBody,
        @Part("pass") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthState>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken)
}

//object PostsApi {
//    val service: PostsApiService by lazy {
//        retrofit.create(PostsApiService::class.java)
//    }
//}