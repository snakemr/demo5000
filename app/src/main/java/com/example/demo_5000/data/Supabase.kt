package com.example.demo_5000.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Слой работы с данными
// Подключение к Supabase, функции регистрации, авторизации, сброса пароля пользователя
// 21.09.2024, Светличный А.А.
class Supabase(url: String, key: String) {
    // подключение к удалённой базе данных
    private val supabase = createSupabaseClient(supabaseUrl = url, supabaseKey = key) {
        install(Postgrest)
        install(GoTrue)
    }

    // регистрация нового пользователя
    suspend fun signUp(name: String, phone: String, mail: String, pass: String) {
        supabase.gotrue.signUpWith(Email) {
            email = mail
            password = pass
            data = buildJsonObject {
                put(NAME, name)
                put(PHONE, phone)
            }
        }
        runCatching {
            supabase.gotrue.modifyUser { phoneNumber = phone }
        }
    }

    // авторизация пользователя
    suspend fun logIn(mail: String, pass: String): String? {
        supabase.gotrue.loginWith(Email) {
            email = mail
            password = pass
        }
        return userName()
    }

    private fun userName() = supabase.gotrue.currentUserOrNull()
        ?.userMetadata
        ?.get(NAME)
        ?.toString()
        ?.removeSurrounding("\"")

    // выход из системы
    suspend fun logout() = supabase.gotrue.logout()

    // отправка кода подтверждения для сброса пароля
    suspend fun sendOTP(mail: String) = supabase.gotrue.sendOtpTo(Email) {
        email = mail
    }

    // проверка кода подтверждения
    suspend fun checkOTP(mail: String, otp: String) =
        supabase.gotrue.verifyEmailOtp(OtpType.Email.RECOVERY, mail, otp)

    // сброс пароля
    suspend fun resetPassword(pass: String) = supabase.gotrue.modifyUser {
        password = pass
    }

    companion object {
        private const val NAME = "display_name"
        private const val PHONE = "phone"
    }
}