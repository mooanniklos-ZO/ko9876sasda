package com.example.data.repository

import com.example.data.local.UserPoemDao
import com.example.data.local.UserPoemEntity
import com.example.data.model.Poem
import com.example.data.remote.RetrofitClient
import com.example.data.poems.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PoemRepository(private val dao: UserPoemDao) {

    val userMetaMap: Flow<Map<Int, UserPoemEntity>> =
        dao.getAllUserMeta().map { list -> list.associateBy { it.poemId } }

    val latestBookmark: Flow<UserPoemEntity?> = dao.getLatestBookmark()

    fun getPoems(): Flow<List<Poem>> = flow {
        try {
            emit(RetrofitClient.apiService.getPoemsFromWebsite())
        } catch (_: Exception) {
            emit(PoemBatch1.poems + PoemBatch2.poems + PoemBatch3.poems + PoemBatch4.poems)
        }
    }

    fun searchPoems(query: String): Flow<List<Poem>> = flow {
        try {
            emit(RetrofitClient.apiService.searchPoemsOnWebsite(query))
        } catch (_: Exception) {
            val localPoems = PoemBatch1.poems + PoemBatch2.poems + PoemBatch3.poems + PoemBatch4.poems
            emit(localPoems.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.fullTextForSearch.contains(query, ignoreCase = true)
            })
        }
    }

    private suspend fun ensureMeta(poemId: Int): UserPoemEntity {
        return dao.getUserMetaDirect(poemId)
            ?: UserPoemEntity(poemId = poemId).also { dao.insertOrUpdate(it) }
    }

    suspend fun toggleFavorite(poemId: Int, currentlyFavorite: Boolean) {
        ensureMeta(poemId)
        dao.setFavorite(poemId, !currentlyFavorite)
    }

    suspend fun toggleBookmark(poemId: Int, currentlyBookmarked: Boolean) {
        ensureMeta(poemId)
        dao.setBookmarked(poemId, !currentlyBookmarked)
    }

    suspend fun saveNote(poemId: Int, note: String) {
        ensureMeta(poemId)
        dao.updateNote(poemId, note)
    }

    suspend fun markAsRead(poemId: Int) {
        ensureMeta(poemId)
        dao.recordRead(poemId, System.currentTimeMillis())
    }
}
