package com.mamelodiinitiative.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthGate(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    content: @Composable (onLogout: () -> Unit) -> Unit
) {
    val state by authViewModel.uiState.collectAsState()

    if (state.user != null) {
        content {
            authViewModel.signOut()
        }
    } else {
        AuthScreen(
            modifier,
            state.isLoading,
            state.message,
            authViewModel::signIn,
            authViewModel::register,
            authViewModel::clearMessage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthScreen(
    modifier: Modifier,
    isLoading: Boolean,
    message: String?,
    onSignIn: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    clearMessage: () -> Unit
) {
    var registerMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = registerMode,
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    },
                    label = "HeaderIcon"
                ) { isRegistering ->
                    Icon(
                        imageVector = if (isRegistering) Icons.Default.PersonAdd else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = primaryColor
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Mamelodi Initiative",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    lineHeight = 36.sp
                ),
                color = primaryColor,
                textAlign = TextAlign.Center
            )

            AnimatedContent(
                targetState = registerMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "Subtitle"
            ) { isRegistering ->
                Text(
                    text = if (isRegistering) "Empowering through education" else "Welcome back! Please sign in",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            onClick = {
                                if (registerMode) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    registerMode = false
                                    clearMessage()
                                }
                            },
                            selected = !registerMode,
                            enabled = !isLoading,
                            label = { 
                                Text("Sign In", maxLines = 1, overflow = TextOverflow.Visible) 
                            },
                            icon = { 
                                Icon(Icons.AutoMirrored.Filled.Login, null, modifier = Modifier.size(18.dp)) 
                            }
                        )
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            onClick = {
                                if (!registerMode) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    registerMode = true
                                    clearMessage()
                                }
                            },
                            selected = registerMode,
                            enabled = !isLoading,
                            label = { 
                                Text("Register", maxLines = 1, overflow = TextOverflow.Visible) 
                            },
                            icon = { 
                                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp)) 
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; clearMessage() },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = primaryColor.copy(alpha = 0.6f)) },
                        singleLine = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; clearMessage() },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = primaryColor.copy(alpha = 0.6f)) },
                        trailingIcon = {
                            IconButton(onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                passwordVisible = !passwordVisible 
                            }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        },
                        singleLine = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (email.isNotBlank() && password.length >= 6) {
                                    focusManager.clearFocus()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (registerMode) onRegister(email, password) else onSignIn(email, password)
                                }
                            }
                        )
                    )

                    AnimatedVisibility(
                        visible = message != null,
                        enter = slideInVertically { it / 2 } + fadeIn(),
                        exit = fadeOut()
                    ) {
                        message?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (registerMode) onRegister(email, password) else onSignIn(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        enabled = !isLoading && email.isNotBlank() && password.length >= 6,
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 4.dp)
                    ) {
                        AnimatedContent(
                            targetState = isLoading to registerMode,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "ButtonContent"
                        ) { (loading, isRegistering) ->
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = if (isRegistering) Icons.Default.ArrowForward else Icons.AutoMirrored.Filled.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = if (isRegistering) "GET STARTED" else "SIGN IN",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 2.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
