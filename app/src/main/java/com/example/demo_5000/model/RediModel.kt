package com.example.demo_5000.model

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo_5000.data.Supabase
import com.example.demo_5000.ui.screens.Screen
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// Слой бизнес-логики. Модель данных. 21.09.2024, Светличный А.А.
class RediModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var screen by mutableStateOf(Screen.Login)        //  сообщение о последней ошибке
    var error by mutableStateOf<String?>(null)  //  текущий экран
    var processing by mutableStateOf(false)     //  идёт загрузка данных
        private set

    var login by mutableStateOf(prefs.getString(LOGIN, null))     // сохранённое имя пользователя
        private set
    var password by mutableStateOf(prefs.getString(PASS, null)?.decode())   // сохраненный пароль
        private set

    private fun request(api: suspend ()->Unit) = viewModelScope.launch {
        processing = true
        runCatching {
            api()
        }.onFailure {
            error = it.message?.substringBefore("\n") ?: "error"
        }
        processing = false
    }

    fun signUp(name: String, phone: String, email: String, pass1: String) = request {
        Supabase.signUp(name, phone, email, pass1)
        screen = Screen.Login
    }

    fun logIn(email: String, pass: String, store: Boolean) = request {
        Supabase.logIn(email, pass)
        login = email
        password = pass
        prefs.edit().apply {
            putString(LOGIN, email)
            if (store) putString(PASS, pass.encode())
        }.apply()
        screen = Screen.Home
    }

    // криптография для пароля
    @OptIn(ExperimentalEncodingApi::class)
    private fun String.encode() = Base64.Mime.encode(toByteArray(Charset.defaultCharset()))
    @OptIn(ExperimentalEncodingApi::class)
    private fun String.decode() = Base64.Mime.decode(this).toString(Charset.defaultCharset())

    companion object {
        const val PREFS = "prefs"
        const val LOGIN = "login"
        const val PASS = "pass"
    }
}