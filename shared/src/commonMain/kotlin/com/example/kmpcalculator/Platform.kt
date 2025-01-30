package com.example.kmpcalculator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform