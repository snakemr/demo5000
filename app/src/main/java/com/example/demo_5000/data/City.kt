package com.example.demo_5000.data

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Long,
    val city: String
)