package com.example.yelimhan.smartorder.network

import com.example.yelimhan.smartorder.model.Menu
import com.example.yelimhan.smartorder.network.model.BaseResponse
import io.reactivex.Observable
import retrofit2.http.*

interface MenuService {
    @GET("menu_list.php")
    fun getMenuList(): Observable<ArrayList<Menu>>

@GET("get_menu_info.php")
fun getMenuInfo(@Query("name") name: String): Observable<Menu>

@GET("recent_menu.php")
fun getRecentMenu(@Query("mCustomer") name: String): Observable<ArrayList<Menu>>

@GET("favorite_menu.php")
fun getFavoriteMenu(@Query("mCustomer") name: String): Observable<Menu>

    @FormUrlEncoded
    @POST("insert_order.php")
    fun insertOrder(@Field("mCustomer") mCustomer: String,
                             @Field("name") name: String,
                             @Field("size") size: String,
                             @Field("type") type: String,
                             @Field("option") option: String,
                             @Field("mindex") mindex: Int,
                             @Field("count") count: Int,
                             @Field("price") price: Int): Observable<BaseResponse>

}