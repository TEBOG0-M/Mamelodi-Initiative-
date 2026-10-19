package com.mamelodiinitiative.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamelodiinitiative.data.sampledata.sampleProgrammes
import com.mamelodiinitiative.navigation.Routes
import com.mamelodiinitiative.ui.components.MamelodiBackButton
import com.mamelodiinitiative.ui.theme.GreenLight
import com.mamelodiinitiative.ui.theme.GreenPrimary
import com.mamelodiinitiative.ui.theme.TextSecondary
import com.mamelodiinitiative.ui.utils.bounceClick

@Composable
fun ProgrammeDetailScreen(programmeId: Int, onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val programme = sampleProgrammes.firstOrNull { it.id == programmeId }
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MamelodiBackButton(onClick = onBack, text = "Back to programmes")

        if (programme == null) {
            Text("Programme not found", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("View programmes") }
        } else {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically()
            ) {
                Surface(shape = RoundedCornerShape(28.dp), color = GreenLight) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Filled.School, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("PROGRAMME", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp), color = GreenPrimary, fontWeight = FontWeight.Black)
                            Text(programme.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("About this programme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = if (programme.description.contains("will be added", ignoreCase = true)) {
                        "Current programme information has not yet been published in the app. Contact Mamelodi Initiative for confirmed details about activities, dates, eligibility and participation."
                    } else programme.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    lineHeight = 26.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Interested in taking part?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Contact the initiative to confirm availability and the next steps for this programme.",
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary
                    )
                    Button(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(Routes.CONTACT) 
                        }, 
                        modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Contact the initiative", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onNavigate(Routes.VOLUNTEER) }, 
                        modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Explore volunteering", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
