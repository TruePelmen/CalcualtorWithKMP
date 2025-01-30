package com.example.kmpcalculator.domain.usecase

import com.example.kmpcalculator.domain.repository.CalculatorRepository

class CalculateExpressionUseCase(private val repository: CalculatorRepository) {
    suspend operator fun invoke(expression: String): Double {
        return repository.calculate(expression)
    }
}