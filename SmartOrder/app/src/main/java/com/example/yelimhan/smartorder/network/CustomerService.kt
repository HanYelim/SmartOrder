package com.example.yelimhan.smartorder.network

import com.example.yelimhan.smartorder.model.Menu
import com.example.yelimhan.smartorder.network.model.BaseResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CustomerService {

    @GET("search_customer.php")
    fun getCustomerInfo(@Query("faceID") name: String): String

    @FormUrlEncoded
    @POST("insert_customer.php")
    fun insertCustomer(@Field("nickname") nickname: String,
                       @Field("faceID") faceID: String): Observable<BaseResponse>
}