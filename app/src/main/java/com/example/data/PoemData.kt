package com.example.data

import com.example.data.model.Poem
import com.example.data.model.PoemCategory
import com.example.data.poems.PoemBatch1
import com.example.data.poems.PoemBatch2
import com.example.data.poems.PoemBatch3
import com.example.data.poems.PoemBatch4

object PoemData {
    val allPoems: List<Poem> by lazy {
        PoemBatch1.poems + PoemBatch2.poems + PoemBatch3.poems + PoemBatch4.poems
    }

    fun getPoemById(id: Int): Poem? {
        return allPoems.find { it.id == id }
    }

    fun getPoemByPage(pageNumber: Int): Poem? {
        return allPoems.find { it.pageNumber == pageNumber }
    }

    fun getPoemsByCategory(category: PoemCategory): List<Poem> {
        if (category == PoemCategory.ALL) return allPoems
        return allPoems.filter { it.category == category }
    }

    fun searchPoems(query: String): List<Poem> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return allPoems
        val normalizedQuery = normalizeArabic(trimmed)
        return allPoems.filter { poem ->
            normalizeArabic(poem.title).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(poem.subTitle).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(poem.dedication).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(poem.notes).contains(normalizedQuery, ignoreCase = true) ||
            poem.verses.any { verse ->
                normalizeArabic(verse.firstHemistich).contains(normalizedQuery, ignoreCase = true) ||
                normalizeArabic(verse.secondHemistich).contains(normalizedQuery, ignoreCase = true)
            }
        }
    }

    fun normalizeArabic(text: String): String {
        return text
            .replace("[\\u064B-\\u065F]".toRegex(), "") // Remove harakat / tashkeel
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
    }

    fun getVerseOfTheDay(): Pair<Poem, String> {
        val poemsList = allPoems
        val dayOfYear = (System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt()
        val poemIndex = kotlin.math.abs(dayOfYear % poemsList.size)
        val selectedPoem = poemsList[poemIndex]
        val verseText = if (selectedPoem.verses.isNotEmpty()) {
            val vIndex = kotlin.math.abs(dayOfYear % selectedPoem.verses.size)
            selectedPoem.verses[vIndex].fullText
        } else {
            selectedPoem.title
        }
        return Pair(selectedPoem, verseText)
    }
}
