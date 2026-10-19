package com.mamelodiinitiative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mamelodiinitiative.navigation.AppNavigation
import com.mamelodiinitiative.ui.theme.MamelodiInitiativeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MamelodiInitiativeTheme { AppNavigation() } }
    }
}
