package com.example.yelimhan.smartorder.network

interface MenuService {
    @GET("menu_list.php")
    fun getMenuList(@Query("driver_id") driverId: Int): Observable<ArrayList<Menu>>

    @GET("get_menu_info.php")
    fun getMenuInfo(@Query("name") name: String): Observable<ArrayList<Menu>>

}