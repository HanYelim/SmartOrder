package com.example.yelimhan.smartorder.network

import com.example.yelimhan.smartorder.model.Menu
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface MenuService {
    @GET("menu_list.php")
    fun getMenuList(@Query("driver_id") driverId: Int): Observable<ArrayList<Menu>>

    @GET("get_menu_info.php")
    fun getMenuInfo(@Query("name") name: String): Observable<ArrayList<Menu>>

    @GET("recent_menu.php")
    fun getRecentMenu(@Query("mCustomer") name: String): Observable<ArrayList<Menu>>

}