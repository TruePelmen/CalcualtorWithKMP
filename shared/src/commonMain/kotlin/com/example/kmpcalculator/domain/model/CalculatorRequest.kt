package com.example.kmpcalculator.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CalculatorRequest(val expression: String)