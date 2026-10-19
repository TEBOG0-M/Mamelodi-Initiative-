package com.mamelodiinitiative.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamelodiinitiative.data.models.NewsItem

@Composable
fun NewsCard(item: NewsItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier) { Column(Modifier.padding(16.dp)) {
        Text(item.title, style = MaterialTheme.typography.titleMedium)
        Text(item.date, style = MaterialTheme.typography.labelMedium)
        Text(item.description, style = MaterialTheme.typography.bodyMedium)
    }}
}
