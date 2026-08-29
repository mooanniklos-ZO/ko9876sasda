package com.example.data.model

enum class PoemCategory(val displayNameAr: String, val iconName: String) {
    ALL("الكل", "AllInclusive"),
    DEVOTIONAL("مناجاة وإيمانيات", "WbSunny"),
    NATIONAL("وطنيات ومجتمع", "Public"),
    PRAISE_ELEGY("مديح ورثاء", "MilitaryTech"),
    LOVE_AFFECTION("غزل ومحبة", "Favorite"),
    WISDOM_COUNSEL("حكم ونصح", "MenuBook"),
    FREE_VERSE_PROSE("شعر حر ونثر", "AutoStories"),
    FAMILY_FRIENDS("الأهل والصحب", "People");

    val arabicName: String get() = displayNameAr
}
