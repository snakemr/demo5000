package com.example.demo_5000.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_5000.R
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.ui.theme.ShowToggle
import com.example.demo_5000.ui.theme.rounded5dp

@Composable
fun SignUp() = Column(Modifier.fillMaxSize().padding(20.dp)) {
    val model = LocalModel.current
    BackHandler { model.screen = Screen.Login }

    Text("Create an account", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    Text("Complete the sign up process to get started",
        Modifier.padding(vertical = 8.dp), Color.Gray)

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass1 by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }
    var show by rememberSaveable { mutableStateOf(false) }
    var agree by rememberSaveable { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(false) }           // активна ли кнопка:

    LaunchedEffect(name, phone, email, pass1, pass2, show, agree, model.processing) {
        enabled = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && pass1.isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && (show || pass1 == pass2)
                && agree
                && !model.processing
    }

    Text("Full name", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("Ivanov Ivan") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
        singleLine = true
    )

    Text("Phone Number", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(phone, { phone = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("+7(999)999-99-99") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
        singleLine = true
    )

    Text("Email Address", Modifier.padding(top = 8.dp, bottom = 4.dp), Color.Gray)
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("***********@mail.com") },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
        singleLine = true
    )

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

    Row(Modifier.padding(vertical = 8.dp)) {
        Checkbox(agree, { agree = it } )
        val terms = "terms"
        val app = LocalContext.current.applicationContext
        val anno = buildAnnotatedString {
            append("By ticking this box, you agree to our ")
            pushStringAnnotation(terms,
                "https://www.termsfeed.com/public/uploads/2021/12/sample-terms-conditions-agreement.pdf")
            withStyle(SpanStyle(MaterialTheme.colorScheme.tertiary)) {
                append("Terms and conditions and private policy")
            }
        }
        ClickableText(anno,  Modifier.padding(vertical = 10.dp),
            style = TextStyle(Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)) {
            anno.getStringAnnotations(terms, it, it).firstOrNull()
                ?.let { url ->
                    Intent(Intent.ACTION_VIEW, Uri.parse(url.item)).apply {
                        flags += Intent.FLAG_ACTIVITY_NEW_TASK
                    }.let(app::startActivity)
                } ?: run {
                    agree = !agree
                }
        }
    }

    Box(Modifier.fillMaxWidth()) {
        Button(onClick = {
            model.signUp(name, phone, email, pass1)
        }, Modifier.fillMaxWidth().focusable(),
            enabled = enabled,      // только когда все данные введены и проверены
            shape = rounded5dp
        ) {
            Text("Sign Up")
        }
        if (model.processing)
            CircularProgressIndicator(Modifier.align(Alignment.Center))
    }

    Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp, bottom = 4.dp)) {
        Text("Already have an account? ", color = Color.Gray)
        Text("Sign in", Modifier.clickable {
            model.screen = Screen.Login
        }, MaterialTheme.colorScheme.primary)
    }

    Text("or sign in using", Modifier.align(Alignment.CenterHorizontally), Color.Gray)
    IconButton(onClick = {
        // TODO
    }, Modifier.align(Alignment.CenterHorizontally)) {
        Image(painterResource(R.drawable.google),
            "login using google", Modifier.size(24.dp))
    }
}