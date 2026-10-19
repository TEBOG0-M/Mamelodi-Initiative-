package com.mamelodiinitiative.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onBackClick: (() -> Unit)? = null) {
    TopAppBar(title = { Text(title) }, navigationIcon = {
        if (onBackClick != null) IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, "Back")
        }
    })
}
