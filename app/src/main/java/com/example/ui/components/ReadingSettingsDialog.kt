package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReadingSettings
import com.example.data.model.ReadingTheme
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldThemeBg
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightThemeBg
import com.example.ui.theme.ParchmentBg
import com.example.ui.theme.RoseThemeBg

@Composable
fun ReadingSettingsDialog(
    settings: ReadingSettings,
    onSettingsChanged: (ReadingSettings) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "خيارات القراءة والتنسيق",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Font Size Slider
                Text(
                    text = "حجم الخط: ${settings.fontSizeSp.toInt()} نقطة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = settings.fontSizeSp,
                    onValueChange = { onSettingsChanged(settings.copy(fontSizeSp = it)) },
                    valueRange = 16f..34f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reading Theme Selector
                Text(
                    text = "سمة صفحة القراءة:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ThemeColorOption(
                        title = "ورقي",
                        bgColor = ParchmentBg,
                        borderColor = EmeraldPrimary,
                        isSelected = settings.theme == ReadingTheme.PARCHMENT,
                        onClick = { onSettingsChanged(settings.copy(theme = ReadingTheme.PARCHMENT)) }
                    )
                    ThemeColorOption(
                        title = "زمردي",
                        bgColor = EmeraldThemeBg,
                        borderColor = GoldAccent,
                        isSelected = settings.theme == ReadingTheme.EMERALD,
                        onClick = { onSettingsChanged(settings.copy(theme = ReadingTheme.EMERALD)) }
                    )
                    ThemeColorOption(
                        title = "ليلي",
                        bgColor = MidnightThemeBg,
                        borderColor = Color.White,
                        isSelected = settings.theme == ReadingTheme.MIDNIGHT,
                        onClick = { onSettingsChanged(settings.copy(theme = ReadingTheme.MIDNIGHT)) }
                    )
                    ThemeColorOption(
                        title = "وردي",
                        bgColor = RoseThemeBg,
                        borderColor = Color(0xFFB5465A),
                        isSelected = settings.theme == ReadingTheme.ROSE,
                        onClick = { onSettingsChanged(settings.copy(theme = ReadingTheme.ROSE)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verse Numbers Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "إظهار أرقام الأبيات", fontSize = 14.sp)
                    Switch(
                        checked = settings.showVerseNumbers,
                        onCheckedChange = { onSettingsChanged(settings.copy(showVerseNumbers = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Decorative Frame Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "إظهار الإطار الزخرفي", fontSize = 14.sp)
                    Switch(
                        checked = settings.showDecorativeFrame,
                        onCheckedChange = { onSettingsChanged(settings.copy(showDecorativeFrame = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("حفظ وإغلاق")
            }
        }
    )
}

@Composable
private fun ThemeColorOption(
    title: String,
    bgColor: Color,
    borderColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) borderColor else Color.Gray.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}
