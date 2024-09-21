package com.example.demo_5000.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_5000.R
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.ui.theme.ShowToggle
import com.example.demo_5000.ui.theme.rounded5dp

@Composable
fun LogIn() = Column(Modifier.fillMaxSize().padding(20.dp)) {
    val model = LocalModel.current

    Spacer(Modifier.weight(1f))
    Text("Welcome back", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    Text("Fill in your email and password to continue",
        Modifier.padding(vertical = 8.dp), Color.Gray)

    var email by rememberSaveable { mutableStateOf(model.login ?: "") }
    var pass by rememberSaveable { mutableStateOf(model.password ?: "") }
    var show by rememberSaveable { mutableStateOf(false) }
    var store by rememberSaveable { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }           // активна ли кнопка:

    LaunchedEffect(email, pass, show, model.processing) {
        enabled = email.isNotEmpty() && pass.isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && !model.processing
    }

    Text("Email Address", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("***********@mail.com") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
        singleLine = true
    )

    Text("Password", Modifier.padding(top = 12.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(pass, { pass = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("••••••••••") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = { ShowToggle(show) { show = it} },
        singleLine = true
    )

    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(store, { store = it }, Modifier.size(32.dp) )
        Text("Remember password", Modifier.weight(1f).clickable { store = !store }, Color.Gray)
        TextButton(onClick = { model.screen = Screen.Forgot }) {
            Text("Forgot password?", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
    }

    Spacer(Modifier.weight(1f))

    Box(Modifier.fillMaxWidth()) {
        Button(onClick = {
            model.logIn(email, pass, store)
        }, Modifier.fillMaxWidth().focusable(),
            enabled = enabled,      // только когда все данные введены и проверены
            shape = rounded5dp
        ) {
            Text("Log In")
        }
        if (model.processing)
            CircularProgressIndicator(Modifier.align(Alignment.Center))
    }

    Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp, bottom = 4.dp)) {
        Text("Don't have an account? ", color = Color.Gray)
        Text("Sign Up", Modifier.clickable {
            model.screen = Screen.SignUp
        }, MaterialTheme.colorScheme.primary)
    }

    Text("or log in using", Modifier.align(Alignment.CenterHorizontally), Color.Gray)
    IconButton(onClick = {
        // TODO
    }, Modifier.align(Alignment.CenterHorizontally)) {
        Image(painterResource(R.drawable.google),
            "login using google", Modifier.size(24.dp))
    }
    Spacer(Modifier.weight(1f))
}