package com.example.demo_5000.ui.screens

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.ui.theme.rounded5dp

@Composable
fun Forgot() = Column(Modifier.fillMaxSize().padding(20.dp)) {
    val model = LocalModel.current
    BackHandler { model.screen = Screen.Login }

    Spacer(Modifier.weight(.25f))
    Text("Forgot Password", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    Text("Enter your email address",
        Modifier.padding(vertical = 8.dp), Color.Gray)

    var email by rememberSaveable { mutableStateOf(model.login ?: "") }
    var enabled by remember { mutableStateOf(false) }           // активна ли кнопка:

    LaunchedEffect(email, model.processing) {
        enabled = email.isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && !model.processing
    }

    Spacer(Modifier.height(20.dp))

    Text("Email Address", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("***********@mail.com") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
        singleLine = true
    )

    Spacer(Modifier.height(50.dp))

    Box(Modifier.fillMaxWidth()) {
        Button(onClick = {
            model.sendOTP(email)
        }, Modifier.fillMaxWidth().focusable(),
            enabled = enabled,      // только когда все данные введены и проверены
            shape = rounded5dp
        ) {
            Text("Send OTP")
        }
        if (model.processing)
            CircularProgressIndicator(Modifier.align(Alignment.Center))
    }

    Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp, bottom = 4.dp)) {
        Text("Remember password? Back to ", color = Color.Gray)
        Text("Sign In", Modifier.clickable {
            model.screen = Screen.Login
        }, MaterialTheme.colorScheme.primary)
    }

    Spacer(Modifier.weight(1f))
}