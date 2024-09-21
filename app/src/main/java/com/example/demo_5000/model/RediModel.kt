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

// Слой бизнес-логики. Модель данных. 21.09.2024, Светличный А.А.
class RediModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var screen by mutableStateOf(Screen.SignUp)       //  сообщение о последней ошибке
    var error by mutableStateOf<String?>(null)  //  текущий экран
    var processing by mutableStateOf(false)     //  идёт загрузка данных
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
    }

    companion object {
        const val PREFS = "prefs"
    }
}