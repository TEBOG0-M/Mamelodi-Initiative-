package com.mamelodiinitiative.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamelodiinitiative.navigation.Routes
import com.mamelodiinitiative.ui.components.MamelodiBackButton
import com.mamelodiinitiative.ui.theme.GreenLight
import com.mamelodiinitiative.ui.theme.GreenPrimary
import com.mamelodiinitiative.ui.theme.TextSecondary
import com.mamelodiinitiative.ui.utils.bounceClick
import kotlinx.coroutines.delay

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "MORE",
            color = GreenPrimary,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Black
        )

        Text(
            text = "Learn more about us",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Find out about the initiative and explore ways to contribute.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        MoreMenuItem(
            title = "About the initiative",
            description = "Learn about the purpose and values behind the work.",
            icon = Icons.Filled.Info,
            onClick = { onNavigate(Routes.ABOUT) }
        )

        MoreMenuItem(
            title = "Support our work",
            description = "Explore practical ways to contribute.",
            icon = Icons.Filled.Favorite,
            onClick = { onNavigate(Routes.DONATE) }
        )

        MoreMenuItem(
            title = "Contact and enquiries",
            description = "Find the available routes for getting involved.",
            icon = Icons.Filled.MailOutline,
            onClick = { onNavigate(Routes.CONTACT) }
        )

        MoreMenuItem(
            title = "Volunteer",
            description = "Share your skills and register your interest.",
            icon = Icons.Filled.People,
            onClick = { onNavigate(Routes.VOLUNTEER) }
        )
        
        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Log out", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MoreMenuItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = Modifier.fillMaxWidth().bounceClick(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = GreenLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = GreenPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    InformationPage(
        eyebrow = "ABOUT US",
        title = "Creating opportunities through community",
        intro = "Mamelodi Initiative is a community-focused initiative supporting youth participation, learning and development.",
        sections = listOf(
            "Our purpose" to
                    "To help connect young people with opportunities to learn, participate, build confidence and explore their potential.",
            "Our approach" to
                    "Community participation and shared learning help create supportive environments where young people feel encouraged to contribute.",
            "Our values" to
                    "Opportunity, inclusion, responsibility, teamwork and respect are our foundations."
        ),
        primaryLabel = "Explore programmes",
        onPrimary = { onNavigate(Routes.PROGRAMMES) },
        secondaryLabel = "Become a volunteer",
        onSecondary = { onNavigate(Routes.VOLUNTEER) },
        onBack = onBack
    )
}

enum class DonationState { SELECTING, AMOUNT, PROCESSING, SUCCESS }

@Composable
fun DonateScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    var donationState by remember { mutableStateOf(DonationState.SELECTING) }
    var selectedPurpose by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val purposes = listOf(
        "General Initiative Support",
        "Youth Education Programmes",
        "Infrastructure and Resources",
        "Holiday Programmes"
    )

    LaunchedEffect(donationState) {
        if (donationState == DonationState.PROCESSING) {
            delay(2000)
            donationState = DonationState.SUCCESS
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (donationState != DonationState.SUCCESS) {
            MamelodiBackButton(onClick = onBack)
        }

        AnimatedContent(
            targetState = donationState,
            transitionSpec = {
                (fadeIn(tween(300)) + scaleIn(initialScale = 0.92f))
                    .togetherWith(fadeOut(tween(300)) + scaleOut(targetScale = 0.92f))
            },
            label = "DonationFlow"
        ) { targetState ->
            when (targetState) {
                DonationState.SELECTING -> {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Text(
                            text = "SUPPORT",
                            color = GreenPrimary,
                            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Choose how to help",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Your support helps us sustain community programmes. Please select a purpose.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        
                        purposes.forEach { purpose ->
                            val isSelected = selectedPurpose == purpose
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) GreenLight else MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) GreenPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clickable { 
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedPurpose = purpose
                                    }
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) GreenPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = purpose,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GreenPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Button(
                            onClick = { donationState = DonationState.AMOUNT },
                            enabled = selectedPurpose != null,
                            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Next Step", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                DonationState.AMOUNT -> {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Text(
                            text = "CONTRIBUTE",
                            color = GreenPrimary,
                            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Enter amount",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Supporting: $selectedPurpose",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                            label = { Text("Amount (ZAR)") },
                            prefix = { Text("R ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        Text(
                            text = "This is a simulated transaction for demonstration purposes. No real funds will be processed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Button(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                donationState = DonationState.PROCESSING 
                            },
                            enabled = amount.isNotBlank() && amount.toInt() > 0,
                            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Donate R$amount Now", fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = { donationState = DonationState.SELECTING },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back to Selection")
                        }
                    }
                }
                DonationState.PROCESSING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CircularProgressIndicator(color = GreenPrimary)
                            Text("Processing Simulated Donation...", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                DonationState.SUCCESS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = GreenLight
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.padding(32.dp).size(64.dp)
                            )
                        }
                        
                        Text(
                            text = "Thank you for your generosity!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Your simulated donation of R$amount towards $selectedPurpose has been recorded in this session.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GreenLight.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "To make a real impact, please use our official channels found on the Contact page.",
                                style = MaterialTheme.typography.bodySmall,
                                color = GreenPrimary,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Button(
                            onClick = { onBack() },
                            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Back to More", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        if (donationState == DonationState.SELECTING) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Other ways to contribute",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = { onNavigate(Routes.VOLUNTEER) },
                modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Explore Volunteering", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ContactScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    InformationPage(
        eyebrow = "CONTACT",
        title = "Find your way to get involved",
        intro = "Use the options below to explore the initiative. Official details should be confirmed through an authorised source.",
        sections = listOf(
            "Programme enquiries" to
                    "Explore the programme descriptions in the app. Confirm activities through an official source.",
            "Volunteering" to
                    "Open the join section to review the application form. Applications are stored for the team.",
            "Donations and support" to
                    "Review the support page for contribution ideas. Verify instructions before sending money."
        ),
        primaryLabel = "View programmes",
        onPrimary = { onNavigate(Routes.PROGRAMMES) },
        secondaryLabel = "Volunteer",
        onSecondary = { onNavigate(Routes.VOLUNTEER) },
        onBack = onBack
    )
}

@Composable
private fun InformationPage(
    eyebrow: String,
    title: String,
    intro: String,
    sections: List<Pair<String, String>>,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        MamelodiBackButton(onClick = onBack)

        Text(
            text = eyebrow,
            color = GreenPrimary,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Black
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(lineHeight = 32.sp),
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = intro,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        sections.forEach { section ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = section.first,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Text(
                        text = section.second,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(primaryLabel, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onSecondary,
            modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(secondaryLabel, fontWeight = FontWeight.Bold)
        }
    }
}
