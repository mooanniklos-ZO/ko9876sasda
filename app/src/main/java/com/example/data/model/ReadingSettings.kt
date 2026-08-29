package com.example.data.model

enum class ReadingTheme(val titleAr: String) {
    PARCHMENT("ورق بردي قديم"),
    EMERALD("زمردي فاخر"),
    MIDNIGHT("ليلي مريح"),
    ROSE("وردي كلاسيكي"),
    CLASSIC_LIGHT("أبيض ناصع")
}

// Alias for compatibility
typealias ReaderTheme = ReadingTheme

enum class VerseDisplayMode(val titleAr: String) {
    TWO_COLUMN("شطرين متقابلين"),
    STACKED("أبيات متتالية"),
    FULL_PAGE("صفحة الكتاب")
}

data class ReadingSettings(
    val fontSizeSp: Float = 20f,
    val lineSpacingMultiplier: Float = 1.6f,
    val theme: ReadingTheme = ReadingTheme.PARCHMENT,
    val displayMode: VerseDisplayMode = VerseDisplayMode.TWO_COLUMN,
    val keepScreenOn: Boolean = true,
    val showVerseNumbers: Boolean = true,
    val showDecorativeFrame: Boolean = true,
    val showDecorators: Boolean = true
) {
    val fontSize: Float get() = fontSizeSp
}
