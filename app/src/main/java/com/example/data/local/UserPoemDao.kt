package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPoemDao {
    @Query("SELECT * FROM user_poem_meta")
    fun getAllUserMeta(): Flow<List<UserPoemEntity>>

    @Query("SELECT * FROM user_poem_meta WHERE poemId = :poemId")
    fun getUserMeta(poemId: Int): Flow<UserPoemEntity?>

    @Query("SELECT * FROM user_poem_meta WHERE poemId = :poemId")
    suspend fun getUserMetaDirect(poemId: Int): UserPoemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(meta: UserPoemEntity)

    @Query("UPDATE user_poem_meta SET isFavorite = :isFavorite WHERE poemId = :poemId")
    suspend fun setFavorite(poemId: Int, isFavorite: Boolean)

    @Query("UPDATE user_poem_meta SET isBookmarked = :isBookmarked WHERE poemId = :poemId")
    suspend fun setBookmarked(poemId: Int, isBookmarked: Boolean)

    @Query("UPDATE user_poem_meta SET note = :note WHERE poemId = :poemId")
    suspend fun updateNote(poemId: Int, note: String)

    @Query("UPDATE user_poem_meta SET lastReadTimestamp = :timestamp, readCount = readCount + 1 WHERE poemId = :poemId")
    suspend fun recordRead(poemId: Int, timestamp: Long)

    @Query("SELECT * FROM user_poem_meta WHERE isBookmarked = 1 ORDER BY lastReadTimestamp DESC LIMIT 1")
    fun getLatestBookmark(): Flow<UserPoemEntity?>
}
