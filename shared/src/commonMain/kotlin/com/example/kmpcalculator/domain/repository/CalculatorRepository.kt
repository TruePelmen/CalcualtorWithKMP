package com.example.kmpcalculator.domain.repository

interface CalculatorRepository {
    suspend fun calculate(expression: String): Double
}