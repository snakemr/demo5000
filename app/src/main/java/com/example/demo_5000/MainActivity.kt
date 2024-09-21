package com.example.demo_5000

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.model.RediModel
import com.example.demo_5000.ui.screens.Screen
import com.example.demo_5000.ui.screens.SignUp
import com.example.demo_5000.ui.theme.Demo5000Theme

class MainActivity : ComponentActivity() {
    private val rediModel by viewModels<RediModel>()  // модель данных
    private val screens = mapOf<Screen, @Composable AnimatedVisibilityScope.()->Unit>(
        Screen.SignUp to { SignUp() }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snack = remember { SnackbarHostState() }
            LaunchedEffect(rediModel.error) {
                rediModel.error?.let {
                    snack.showSnackbar(it,
                        withDismissAction = true, duration = SnackbarDuration.Indefinite)
                    rediModel.error = null
                }
            }
            Demo5000Theme {
                CompositionLocalProvider(LocalModel provides rediModel) {
                    Scaffold(snackbarHost = { SnackbarHost(snack) }) {
                        for (screen in screens) {
                            AnimatedVisibility(rediModel.screen == screen.key,
                                Modifier.padding(it), enter = fadeIn(), exit = fadeOut(),
                                label = screen.key.name, content = screen.value
                            )
                        }
                    }
                }
            }
        }
    }
}
