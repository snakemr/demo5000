package com.example.demo_5000.model

import android.app.Application
import androidx.compose.runtime.compositionLocalOf

val LocalModel = compositionLocalOf { RediModel(Application()) }