package com.mamelodiinitiative.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamelodiinitiative.data.sampledata.sampleNews
import com.mamelodiinitiative.data.sampledata.sampleProgrammes
import com.mamelodiinitiative.navigation.Routes
import com.mamelodiinitiative.ui.theme.GreenDark
import com.mamelodiinitiative.ui.theme.GreenLight
import com.mamelodiinitiative.ui.theme.GreenPrimary
import com.mamelodiinitiative.ui.theme.TextSecondary

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "MAMELODI INITIATIVE",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                    color = GreenPrimary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Building brighter futures, together.",
                    style = MaterialTheme.typography.headlineMedium.copy(lineHeight = 32.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Discover our programmes, follow our latest updates, or get involved in the community.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, 200)) + slideInVertically(tween(600, 200)) { 40 }
        ) {
            FriendlyCard(
                onClick = { onNavigate(Routes.VOLUNTEER) },
                backgroundColor = GreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "MAKE A DIFFERENCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFDCEFE3),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your time can open doors.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Volunteer with us and help create opportunities for young people.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(Routes.VOLUNTEER) 
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = GreenDark
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(top = 4.dp).bounceClick()
                    ) {
                        Text("Explore volunteering", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(2.dp))
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionHeading(title = "Explore", subtitle = "Find your next step")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    title = "Learn",
                    icon = { Icon(Icons.Filled.School, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.PROGRAMMES) }
                )
                QuickAction(
                    title = "Join",
                    icon = { Icon(Icons.Filled.People, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.VOLUNTEER) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    title = "Latest news",
                    icon = { Icon(Icons.Filled.Article, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.NEWS) }
                )
                QuickAction(
                    title = "Support us",
                    icon = { Icon(Icons.Filled.FavoriteBorder, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Routes.DONATE) }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionHeading(title = "Our programmes", subtitle = "Explore ways to learn and grow")
            sampleProgrammes.take(3).forEachIndexed { index, programme ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, 400 + (index * 100))) + slideInHorizontally(tween(600, 400 + (index * 100))) { 40 }
                ) {
                    FriendlyCard(
                        onClick = { onNavigate(Routes.programmeDetail(programme.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GreenLight
                            ) {
                                Icon(
                                    Icons.Filled.School,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(programme.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(programme.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                            }
                            Icon(Icons.Filled.ArrowForward, contentDescription = "View ${programme.title}", tint = GreenPrimary, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = { onNavigate(Routes.PROGRAMMES) },
                modifier = Modifier.fillMaxWidth().bounceClick(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("View all programmes", fontWeight = FontWeight.SemiBold)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionHeading(title = "Latest update", subtitle = "News from the initiative")
            val latest = sampleNews.firstOrNull()
            if (latest != null) {
                FriendlyCard(
                    onClick = { onNavigate(Routes.newsDetail(latest.id)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(latest.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(latest.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 3)
                        Text("Read update  →", style = MaterialTheme.typography.labelLarge, color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }

        Text(
            text = "Learn. Participate. Make an impact.",
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Modifier.bounceClick() = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "bounce")
    
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    
    LaunchedEffect(pressed) {
        isPressed = pressed
    }

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
private fun FriendlyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Card(
        onClick = { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick() 
        },
        modifier = modifier.bounceClick(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun QuickAction(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FriendlyCard(
        onClick = onClick,
        modifier = modifier.height(110.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = GreenLight) {
                Box(Modifier.padding(8.dp)) {
                    CompositionLocalProvider(
                        LocalContentColor provides GreenPrimary
                    ) { icon() }
                }
            }
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}
