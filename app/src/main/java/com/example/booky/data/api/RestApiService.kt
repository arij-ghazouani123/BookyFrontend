package com.example.booky.data.api
import com.example.booky.data.models.Book
import com.example.booky.data.models.BookList
import com.example.booky.data.models.User
import com.example.booky.data.models.deletebook
import com.example.booky.data.models.sendmail
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface RestApiService {


    @POST("user/login")
    fun seConnecter(@Body info: RequestBody): Call<User>


    @POST("user/register")
    fun SignIn(@Body info: RequestBody): Call<User>
    @PUT("user/updateVerififed")
    fun updateVerified(@Body info: RequestBody): Call<User>

    @POST("user/getuserEmail")
    fun getuserbyemail(@Body info: RequestBody): Call<MutableList<User>>

    @PUT("user/update")
    fun updateusernotpass(@Body info: RequestBody): Call<User>

    @POST("user/sendmail")
    fun sendmail(@Body info: RequestBody): Call<sendmail>

    @PUT("user/newpass")
    fun UpdateUser(@Body info: RequestBody): Call<User>

    @Multipart
    @POST("/book/profileimage/{id}")
    fun uploadimagebook(@Part image: MultipartBody.Part, @Path("id") id : String): Call<Book>
    @POST("/book/addBook")
    fun createbook(@Body info: RequestBody): Call<Book>

    @GET("/book/getAllBooks")
    fun AllBooks(): Call<MutableList<BookList>>

    @DELETE("/book/deleteBook/{id}")
    fun deletebook(@Path("id") id : String): Call<deletebook>

    @POST("/book/updateBook/{id}")
    fun Updatbook(@Body info: RequestBody,@Path("id") id : String): Call<Book>

    @Multipart
    @POST("/user/profileimage/{id}")
    fun upload(@Part image: MultipartBody.Part,@Path("id") id : String): Call<User>
    //*********************** Sign up/in ***********************//
    @Headers("Content-Type:application/json")
    @POST("user/login")
    fun loginUser(@Body info: User): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("user/register")
    fun registerUser(
        @Body info: User
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("user/verify")
    fun verifyUser(
        @Body info: User
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("user/newverify")
    fun NewverifyUser(
        @Body info: User
    ): Call<ResponseBody>



    @Headers("Content-Type:application/json")
    @POST("user/reset")
    fun ResetPassEmail(
        @Body info: User
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @PATCH("user/newpassemail")
    fun UpdatePass(
        @Body info: User
    ): Call<ResponseBody>




}


class RetrofitInstance {
    companion object {


        //const val BASE_URL: String = "https://booky-xomc.onrender.com/"
        const val BASE_URL: String = "http://10.0.2.2:9090/"


        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}