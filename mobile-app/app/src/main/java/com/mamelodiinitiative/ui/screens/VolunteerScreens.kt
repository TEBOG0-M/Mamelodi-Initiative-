package com.mamelodiinitiative.ui.screens

import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamelodiinitiative.data.models.VolunteerApplication
import com.mamelodiinitiative.navigation.Routes
import com.mamelodiinitiative.ui.components.MamelodiBackButton
import com.mamelodiinitiative.ui.theme.GreenLight
import com.mamelodiinitiative.ui.theme.GreenPrimary
import com.mamelodiinitiative.ui.theme.TextSecondary
import com.mamelodiinitiative.ui.utils.bounceClick
import com.mamelodiinitiative.viewmodel.VolunteerUiState

@Composable
fun VolunteerScreen(onNavigate: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            "VOLUNTEER WITH US",
            color = GreenPrimary,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Black
        )
        Text(
            "Your time can open doors.",
            style = MaterialTheme.typography.headlineMedium.copy(lineHeight = 32.sp),
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            "Share your skills, encourage young people and help create opportunities across the community.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GreenLight),
            modifier = Modifier.bounceClick()
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Make your contribution count",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tell us how to contact you and the ways you would like to help.",
                    color = TextSecondary
                )
                Button(
                    onClick = { onNavigate(Routes.VOLUNTEER_FORM) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Apply to volunteer", fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("Ways you can help", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Benefit(Icons.Filled.Groups, "Support young people", "Help create welcoming spaces where young people can learn, connect and grow.")
        Benefit(Icons.Filled.Lightbulb, "Share your skills", "Contribute your experience, interests or practical skills to community activities.")
        Benefit(Icons.Filled.Favorite, "Strengthen the community", "Work alongside others who want to make a constructive difference.")
        
        Spacer(Modifier.height(8.dp))
    }
}

@Composable private fun Benefit(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(shape = RoundedCornerShape(12.dp), color = GreenLight) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.padding(12.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
fun VolunteerApplicationScreen(application: VolunteerApplication, onChange: (VolunteerApplication) -> Unit, onBack: () -> Unit, onReview: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val validEmail = Patterns.EMAIL_ADDRESS.matcher(application.email.trim()).matches()
    val validPhone = application.phone.filter { it.isDigit() }.length >= 7
    val canReview = application.fullName.isNotBlank() && validEmail && validPhone && application.availability.isNotBlank()
    
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MamelodiBackButton(onClick = onBack, text = "Back to volunteering")
        
        Text("Volunteer application", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Complete your details so we can get in touch.", color = TextSecondary)

        // Sectioned Groups for better UX
        FormSection(title = "Personal Information") {
            FriendlyTextField(
                value = application.fullName,
                onValueChange = { onChange(application.copy(fullName = it)) },
                label = "Full name *",
                isValid = application.fullName.isNotBlank()
            )
            FriendlyTextField(
                value = application.email,
                onValueChange = { onChange(application.copy(email = it)) },
                label = "Email address *",
                keyboardType = KeyboardType.Email,
                isValid = validEmail,
                errorText = if (application.email.isNotBlank() && !validEmail) "Enter a valid email address" else null
            )
            FriendlyTextField(
                value = application.phone,
                onValueChange = { onChange(application.copy(phone = it)) },
                label = "Phone number *",
                keyboardType = KeyboardType.Phone,
                isValid = validPhone
            )
        }

        FormSection(title = "Your Interests") {
            FriendlyTextField(
                value = application.skills,
                onValueChange = { onChange(application.copy(skills = it)) },
                label = "Skills or interests",
                placeholder = "e.g. tutoring, sport, events, admin",
                minLines = 2
            )
            FriendlyTextField(
                value = application.programmePreference,
                onValueChange = { onChange(application.copy(programmePreference = it)) },
                label = "Programme of interest (optional)"
            )
            FriendlyTextField(
                value = application.availability,
                onValueChange = { onChange(application.copy(availability = it)) },
                label = "Availability *",
                placeholder = "Which days or times are you available?",
                minLines = 2,
                isValid = application.availability.isNotBlank()
            )
        }
        
        Text("Required: full name, valid email, phone number and availability.", color = TextSecondary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onReview()
            },
            enabled = canReview,
            modifier = Modifier.fillMaxWidth().height(58.dp).bounceClick(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Review application", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GreenPrimary)
            content()
        }
    }
}

@Composable
private fun FriendlyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    isValid: Boolean? = null,
    errorText: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = minLines == 1,
            minLines = minLines,
            isError = errorText != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                if (isValid == true) {
                    Icon(Icons.Default.Check, "Valid", tint = GreenPrimary)
                }
            }
        )
        if (errorText != null) {
            Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun VolunteerReviewScreen(
    state: VolunteerUiState,
    onEdit: () -> Unit,
    onSubmit: () -> Unit,
    onSuccess: () -> Unit
) {
    val application = state.application
    val haptic = LocalHapticFeedback.current
    
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Review application", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Check your details before submitting.", color = TextSecondary)
        
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                ReviewField("Full name", application.fullName)
                ReviewField("Email", application.email)
                ReviewField("Phone", application.phone)
                ReviewField("Skills and interests", application.skills.ifBlank { "Not provided" })
                ReviewField("Programme of interest", application.programmePreference.ifBlank { "No preference selected" })
                ReviewField("Availability", application.availability)
            }
        }

        if (state.error != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                    Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Text("By continuing, you confirm these details are accurate.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSubmit()
            },
            modifier = Modifier.fillMaxWidth().height(58.dp).bounceClick(),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(18.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Submit application", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth().height(58.dp).bounceClick(),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(18.dp)
        ) { 
            Text("Edit details", fontWeight = FontWeight.Bold) 
        }
        
        Spacer(Modifier.height(12.dp))
    }
}

@Composable private fun ReviewField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = TextSecondary, fontWeight = FontWeight.SemiBold)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun VolunteerSuccessScreen(onNavigate: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Surface(shape = RoundedCornerShape(32.dp), color = GreenLight) {
                Icon(
                    Icons.Filled.CheckCircle,
                    null,
                    tint = GreenPrimary,
                    modifier = Modifier.padding(32.dp).size(64.dp)
                )
            }
            Text("Application complete", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(
                "Thank you for offering your time and skills. Your application has been successfully submitted.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onNavigate(Routes.HOME) },
                    modifier = Modifier.fillMaxWidth().height(58.dp).bounceClick(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Return to Home", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onNavigate(Routes.PROGRAMMES) },
                    modifier = Modifier.fillMaxWidth().height(58.dp).bounceClick(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Explore programmes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
