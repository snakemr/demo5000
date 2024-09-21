package com.example.demo_5000.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Слой работы с данными
// Подключение к Supabase, функции регистрации, авторизации, сброса пароля пользователя
// 21.09.2024, Светличный А.А.
object Supabase {
    // подключение к удалённой базе данных
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://xsdviehjoqcbpsnucgfm.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhzZHZpZWhqb3FjYnBzbnVjZ2ZtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjY2MjQ3MzUsImV4cCI6MjA0MjIwMDczNX0.A_t_a8_HQL0aA4EZmdjJ8JF-KW7estH48-KprVNVFvE"
    ) {
        install(Postgrest)
        install(GoTrue)
    }

    private const val NAME = "display_name"
    private const val PHONE = "phone"

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
}