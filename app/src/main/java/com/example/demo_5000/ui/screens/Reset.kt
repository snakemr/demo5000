package com.example.demo_5000.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.ui.theme.ShowToggle
import com.example.demo_5000.ui.theme.rounded5dp

@Composable
fun Reset() = Column(Modifier.fillMaxSize().padding(20.dp)) {
    val model = LocalModel.current
    BackHandler { model.screen = Screen.Login }

    Spacer(Modifier.weight(.35f))
    Text("New Password", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    Text("Enter new password",
        Modifier.padding(vertical = 8.dp), Color.Gray)

    var pass1 by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }
    var show by rememberSaveable { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }           // активна ли кнопка:

    LaunchedEffect(pass1, pass2, show, model.processing) {
        enabled = pass1.isNotEmpty()
                && (show || pass1 == pass2)
                && !model.processing
    }

    Spacer(Modifier.height(40.dp))

    Text("Password", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(pass1, { pass1 = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("••••••••••") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = { ShowToggle(show) { show = it} },
        singleLine = true
    )

    AnimatedVisibility(!show) {
        Column {
            Text("Confirm Password", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
            OutlinedTextField(pass2, { pass2 = it }, Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••••") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { ShowToggle(show) { show = it } },
                singleLine = true
            )
        }
    }

    Spacer(Modifier.height(50.dp))

    Box(Modifier.fillMaxWidth()) {
        Button(onClick = {
            model.resetPassword(pass1)
        }, Modifier.fillMaxWidth().focusable(),
            enabled = enabled,      // только когда все данные введены и проверены
            shape = rounded5dp
        ) {
            Text("Log In")
        }
        if (model.processing)
            CircularProgressIndicator(Modifier.align(Alignment.Center))
    }

    Spacer(Modifier.weight(1f))
}