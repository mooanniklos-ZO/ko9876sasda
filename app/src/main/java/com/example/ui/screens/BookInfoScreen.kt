package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.PoemViewModel

@Composable
fun BookInfoScreen(viewModel: PoemViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "وللشعر حلاوة كحلاوة السكر",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Card {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("المؤلف والشاعر: د. مالك عبدالرحمن الرميمة", style = MaterialTheme.typography.titleMedium)
                Text("ديوان شعري رقمي مع القراءة والتلاوة الصوتية والمفضلة والعلامات ودفتر الخواطر.")
                Text("إجمالي القصائد: ${viewModel.filteredPoems.value.size}")
            }
        }
    }
}
