package com.example.data.model

data class Verse(
    val number: Int,
    val firstHemistich: String, // الشطر الأول (الصدر)
    val secondHemistich: String = "" // الشطر الثاني (العجز)
) {
    val order: Int get() = number

    val fullText: String
        get() = if (secondHemistich.isNotBlank()) "$firstHemistich ... $secondHemistich" else firstHemistich
}

data class Poem(
    val id: Int,
    val pageNumber: Int,
    val title: String,
    val subTitle: String = "",
    val category: PoemCategory,
    val verses: List<Verse>,
    val dateString: String = "",
    val dedication: String = "",
    val author: String = "الدكتور / مالك عبدالرحمن الرميمة",
    val notes: String = ""
) {
    val totalVerses: Int get() = verses.size
    
    val fullTextForSearch: String get() = buildString {
        append(title).append(" ")
        append(subTitle).append(" ")
        append(dedication).append(" ")
        verses.forEach {
            append(it.firstHemistich).append(" ")
            append(it.secondHemistich).append(" ")
        }
        append(notes)
    }
}
