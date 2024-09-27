package com.example.demo_5000.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.demo_5000.model.LocalModel
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    val model = LocalModel.current
    val cities by model.cities(realtime = true).collectAsState(emptyList(), Dispatchers.IO)

    Text("Welcome, ${model.firstname}")

    Box(Modifier.fillMaxWidth().aspectRatio(1f).align(Alignment.TopCenter),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(cities) {
                Text(it.city, Modifier.combinedClickable(
                    onLongClick = { model.delete(it) },
                    onClick = { model.update(it.copy(city = it.city + "!")) }
                ))
            }
            item {
                TextButton({ model.insert("Махачкала") }) { Text("+") }
            }
        }
    }

    Box(Modifier.fillMaxWidth().aspectRatio(1f).align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = { model.logout() }) {
            Text("Log out", fontWeight = FontWeight.Normal)
        }
    }
}