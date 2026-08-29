package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_poem_meta")
data class UserPoemEntity(
    @PrimaryKey val poemId: Int,
    val isFavorite: Boolean = false,
    val isBookmarked: Boolean = false,
    val note: String = "",
    val lastReadTimestamp: Long = 0L,
    val readCount: Int = 0
)
