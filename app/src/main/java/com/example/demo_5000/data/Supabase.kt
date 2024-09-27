package com.example.demo_5000.data

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Слой работы с данными
// Подключение к Supabase, функции регистрации, авторизации, сброса пароля пользователя
// 21.09.2024, Светличный А.А.
class Supabase(url: String, key: String) {
    // подключение к удалённой базе данных
    private val supabase = createSupabaseClient(supabaseUrl = url, supabaseKey = key) {
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }

    // регистрация нового пользователя
    suspend fun signUp(name: String, phone: String, mail: String, pass: String) {
        supabase.auth.signUpWith(Email) {
            email = mail
            password = pass
            data = buildJsonObject {
                put(NAME, name)
                put(PHONE, phone)
            }
        }
        runCatching {
            supabase.auth.updateUser { this.phone = phone }
        }
    }

    // авторизация пользователя
    suspend fun logIn(mail: String, pass: String): String? {
        supabase.auth.signInWith(Email) {
            email = mail
            password = pass
        }
        return userName()
    }

    private fun userName() = supabase.auth.currentUserOrNull()
        ?.userMetadata
        ?.get(NAME)
        ?.toString()
        ?.removeSurrounding("\"")

    // выход из системы
    suspend fun logout() = supabase.auth.signOut()

    // отправка кода подтверждения для сброса пароля
    suspend fun sendOTP(mail: String) = supabase.auth.signInWith(OTP) {
        email = mail
    }

    // проверка кода подтверждения
    suspend fun checkOTP(mail: String, otp: String) =
        supabase.auth.verifyEmailOtp(OtpType.Email.RECOVERY, mail, otp)

    // сброс пароля
    suspend fun resetPassword(pass: String) = supabase.auth.updateUser {
        password = pass
    }

    suspend fun cities() = supabase.from(CITIES).select().decodeList<City>()

    @OptIn(SupabaseExperimental::class)
    fun cityFlow() = supabase.from(CITIES).selectAsFlow(City::id)

    @Serializable private data class InsertCity(val city: String)
    suspend fun insert(name: String) = supabase.from(CITIES).insert(InsertCity(name))

    suspend fun delete(city: City) = supabase.from(CITIES).delete {
        filter { City::id eq city.id }
    }

    suspend fun update(city: City) = supabase.from(CITIES).update({
        City::city setTo city.city
    }) {
        filter { City::id eq city.id }
    }

    companion object {
        private const val NAME = "display_name"
        private const val PHONE = "phone"
        private const val CITIES = "cities"
    }
}