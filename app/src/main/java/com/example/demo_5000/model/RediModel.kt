package com.example.demo_5000.model

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo_5000.R
import com.example.demo_5000.data.City
import com.example.demo_5000.data.Crypto
import com.example.demo_5000.data.Supabase
import com.example.demo_5000.ui.screens.Screen
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

// Слой бизнес-логики. Модель данных. 21.09.2024, Светличный А.А.
class RediModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val supabase = app.resources.run {
        Supabase(getString(R.string.supabaseUrl), getString(R.string.supabaseKey))
    }

    var screen by mutableStateOf(Screen.Start)        //  сообщение о последней ошибке
    var error by mutableStateOf<String?>(null)  //  текущий экран
    var processing by mutableStateOf(false)     //  идёт загрузка данных
        private set

    private val iv = with(Crypto) { prefs.getString(IV, null).toIV() }

    var login by mutableStateOf(prefs.getString(LOGIN, null))     // сохранённое имя пользователя
        private set
    var password by mutableStateOf(prefs.getString(PASS, null)?.decode())   // сохраненный пароль
        private set
    var firstname by mutableStateOf("")
        private set

    init {
        login?.let { email ->
            password?.let { pass ->
                logIn(email, pass, store = false)
            }
        }
    }

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
        supabase.signUp(name, phone, email, pass1)
        screen = Screen.Login
    }

    fun logIn(email: String, pass: String, store: Boolean) = request {
        firstname = supabase.logIn(email, pass) ?: email
        login = email
        password = pass
        prefs.edit().apply {
            putString(LOGIN, email)
            if (store) putString(PASS, pass.encode())
        }.apply()
        screen = Screen.Home
    }

    fun sendOTP(email: String = login ?: "") = request {
        supabase.sendOTP(email)
        login = email
        screen = Screen.OTP
    }

    fun checkOTP(code: String) = request {
        login?.let {
            supabase.checkOTP(it, code)
            screen = Screen.Reset
        }
    }

    fun resetPassword(pass: String) = request {
        supabase.resetPassword(pass)
        password = null
        prefs.edit().remove(PASS).apply()
        screen = Screen.Login
    }

    fun logout() = request {
        supabase.logout()
        login = null
        password = null
        prefs.edit().remove(LOGIN).remove(PASS).apply()
        screen = Screen.Login
    }

    fun cities(realtime: Boolean = false) = if (realtime)
        supabase.cityFlow()
    else flow {
        emit(supabase.cities())
    }

    fun insert(name: String) = request {
        supabase.insert(name)
    }

    fun delete(city: City) = request {
        supabase.delete(city)
    }

    fun update(city: City) = request {
        supabase.update(city)
    }

    // криптография для пароля
    @OptIn(ExperimentalEncodingApi::class)
    private fun String.encode() = with(Crypto) {
        encode {
            prefs.edit().putString(IV, it).apply()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun String.decode() = with(Crypto) {
        runCatching { decode(iv) }.getOrElse { "" }
    }

    companion object {
        private const val PREFS = "prefs"
        private const val LOGIN = "login"
        private const val PASS = "pass"
        private const val IV = "IV"
    }
}