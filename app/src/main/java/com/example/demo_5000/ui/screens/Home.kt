package com.example.demo_5000.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.demo_5000.model.LocalModel

@Composable
fun Home() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    val model = LocalModel.current
    Text("Home")
    Box(Modifier.fillMaxWidth().aspectRatio(1f).align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = { model.logout() }) {
            Text("Log out", fontWeight = FontWeight.Normal)
        }
    }
}