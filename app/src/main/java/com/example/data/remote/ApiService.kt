package com.example.data.remote

import com.example.data.model.Poem
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    // رابط جلب القصائد من موقعك
    @GET("api/poems.php") 
    suspend fun getPoemsFromWebsite(): List<Poem>

    // رابط البحث في موقعك
    @GET("api/search.php")
    suspend fun searchPoemsOnWebsite(@Query("q") query: String): List<Poem>
}
