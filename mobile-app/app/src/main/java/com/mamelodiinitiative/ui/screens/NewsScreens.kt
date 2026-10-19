package com.mamelodiinitiative.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamelodiinitiative.data.sampledata.sampleNews
import com.mamelodiinitiative.navigation.Routes
import com.mamelodiinitiative.ui.components.MamelodiBackButton
import com.mamelodiinitiative.ui.theme.GreenLight
import com.mamelodiinitiative.ui.theme.GreenPrimary
import com.mamelodiinitiative.ui.theme.TextSecondary
import com.mamelodiinitiative.ui.utils.bounceClick

@Composable
fun NewsScreen(onNavigate: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { -20 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "NEWS & STORIES",
                    color = GreenPrimary,
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Ideas, people and community",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Explore stories about youth development, volunteering, and community participation.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        sampleNews.forEachIndexed { index, news ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 100 * index)) + slideInVertically(tween(500, 100 * index)) { 40 }
            ) {
                Card(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigate(Routes.newsDetail(news.id)) 
                    },
                    modifier = Modifier.fillMaxWidth().bounceClick(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = GreenLight,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Article, null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
                                Text(news.date, color = GreenPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(news.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(news.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 3)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Read story", color = GreenPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                            Icon(Icons.Filled.ArrowForward, null, tint = GreenPrimary, modifier = Modifier.padding(start = 6.dp).size(18.dp))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun NewsDetailScreen(newsId: Int, onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val news = sampleNews.firstOrNull { it.id == newsId }
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MamelodiBackButton(onClick = onBack, text = "Back to news")

        if (news == null) {
            Text("Story not found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("View news") }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(news.date.uppercase(), color = GreenPrimary, style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp), fontWeight = FontWeight.Black)
                Text(news.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GreenLight,
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    Icons.Filled.Article, null, tint = GreenPrimary,
                    modifier = Modifier.padding(48.dp).size(64.dp).align(Alignment.CenterHorizontally)
                )
            }

            Text(news.description, style = MaterialTheme.typography.bodyLarge, color = TextSecondary, lineHeight = 26.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Get involved", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Interested in contributing? Explore the ways to participate.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(Routes.VOLUNTEER) 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Join as a Volunteer", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { onNavigate(Routes.PROGRAMMES) },
                        modifier = Modifier.fillMaxWidth().bounceClick()
                    ) {
                        Text("Explore Programmes", fontWeight = FontWeight.Bold, color = GreenPrimary)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
