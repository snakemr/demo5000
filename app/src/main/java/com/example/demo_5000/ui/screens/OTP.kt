package com.example.demo_5000.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_5000.model.LocalModel
import com.example.demo_5000.ui.custom.PinField
import com.example.demo_5000.ui.theme.rounded5dp
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun OTP() = Column(Modifier.fillMaxSize().padding(20.dp)) {
    val model = LocalModel.current
    BackHandler {
        model.screen = Screen.Forgot
    }

    Spacer(Modifier.fillMaxSize(.15f))

    Text("OTP Verification", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    Text("Enter the 6 digit numbers sent to your email", Modifier.padding(vertical = 8.dp), Color.Gray)

    var code by remember { mutableStateOf<Int?>(null) }
    var enabled by remember { mutableStateOf(false) }

    val minute = 60_000L
    val second = 1_000L
    var counter by remember { mutableLongStateOf(minute) }
    var time by remember { mutableLongStateOf(System.currentTimeMillis() + counter) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(second)
            counter = max(time - System.currentTimeMillis(), 0)
        }
    }

    LaunchedEffect(code, model.processing) {
        enabled = code != null && !model.processing
    }

    PinField(Modifier.padding(vertical = 40.dp)) {
        code = it
    }

    Row(Modifier.align(Alignment.CenterHorizontally).padding(bottom = 40.dp)) {
        Text("If you didn’t receive code, ", color = Color.Gray, fontSize = 14.sp)
        if (counter > 0)
            Text("resend after ${counter/minute}:" +
                    (counter%minute/second).toString().padStart(2, '0'),
                color = Color.Gray, fontSize = 14.sp)
        else
            Text("resend", Modifier.clickable {
                model.sendOTP()
                counter = minute
                time = System.currentTimeMillis() + counter
            }, MaterialTheme.colorScheme.primary, 14.sp)
    }

    Button(onClick = {
        code?.let {
            model.checkOTP(it.toString())
        }
    }, Modifier.fillMaxWidth().focusable(), enabled, shape = rounded5dp) {
        Text("Set New Password")
    }

    Box(Modifier.weight(1f).fillMaxWidth()) {
        if (model.processing) CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}