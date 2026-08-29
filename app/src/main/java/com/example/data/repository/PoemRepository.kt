package com.example.data.repository

import com.example.data.model.Poem
import com.example.data.remote.RetrofitClient
import com.example.data.poems.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PoemRepository {

    // دالة جلب القصائد من الموقع مع إمكانية استخدام البيانات المحلية كاحتياطي
    fun getPoems(): Flow<List<Poem>> = flow {
        try {
            // محاولة الجلب من الموقع أولاً
            val remotePoems = RetrofitClient.apiService.getPoemsFromWebsite()
            emit(remotePoems)
        } catch (e: Exception) {
            // في حال عدم وجود إنترنت أو فشل الموقع، جلب البيانات المحلية القديمة
            val localPoems = PoemBatch1.poems + PoemBatch2.poems + PoemBatch3.poems + PoemBatch4.poems
            emit(localPoems)
        }
    }

    // دالة البحث عبر الموقع
    fun searchPoems(query: String): Flow<List<Poem>> = flow {
        try {
            val results = RetrofitClient.apiService.searchPoemsOnWebsite(query)
            emit(results)
        } catch (e: Exception) {
            val localPoems = PoemBatch1.poems + PoemBatch2.poems + PoemBatch3.poems + PoemBatch4.poems
            val filtered = localPoems.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) 
            }
            emit(filtered)
        }
    }
}
