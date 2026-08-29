package com.example.data.repository

import com.example.data.PoemData
import com.example.data.local.UserPoemDao
import com.example.data.local.UserPoemEntity
import com.example.data.model.Poem
import com.example.data.model.PoemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PoemRepository(private val userPoemDao: UserPoemDao) {

    fun getAllPoems(): List<Poem> = PoemData.allPoems

    fun getPoemById(id: Int): Poem? = PoemData.getPoemById(id)

    fun search(query: String): List<Poem> = PoemData.searchPoems(query)

    fun getByCategory(category: PoemCategory): List<Poem> = PoemData.getPoemsByCategory(category)

    val userMetaMap: Flow<Map<Int, UserPoemEntity>> = userPoemDao.getAllUserMeta().map { list ->
        list.associateBy { it.poemId }
    }

    fun getPoemMeta(poemId: Int): Flow<UserPoemEntity?> = userPoemDao.getUserMeta(poemId)

    suspend fun toggleFavorite(poemId: Int, currentlyFavorite: Boolean) {
        val existing = userPoemDao.getUserMetaDirect(poemId)
        if (existing == null) {
            userPoemDao.insertOrUpdate(
                UserPoemEntity(poemId = poemId, isFavorite = !currentlyFavorite)
            )
        } else {
            userPoemDao.setFavorite(poemId, !currentlyFavorite)
        }
    }

    suspend fun toggleBookmark(poemId: Int, currentlyBookmarked: Boolean) {
        val existing = userPoemDao.getUserMetaDirect(poemId)
        if (existing == null) {
            userPoemDao.insertOrUpdate(
                UserPoemEntity(poemId = poemId, isBookmarked = !currentlyBookmarked, lastReadTimestamp = System.currentTimeMillis())
            )
        } else {
            userPoemDao.setBookmarked(poemId, !currentlyBookmarked)
        }
    }

    suspend fun saveNote(poemId: Int, note: String) {
        val existing = userPoemDao.getUserMetaDirect(poemId)
        if (existing == null) {
            userPoemDao.insertOrUpdate(
                UserPoemEntity(poemId = poemId, note = note)
            )
        } else {
            userPoemDao.updateNote(poemId, note)
        }
    }

    suspend fun markAsRead(poemId: Int) {
        val existing = userPoemDao.getUserMetaDirect(poemId)
        val now = System.currentTimeMillis()
        if (existing == null) {
            userPoemDao.insertOrUpdate(
                UserPoemEntity(poemId = poemId, lastReadTimestamp = now, readCount = 1)
            )
        } else {
            userPoemDao.recordRead(poemId, now)
        }
    }

    val latestBookmark: Flow<UserPoemEntity?> = userPoemDao.getLatestBookmark()
}
