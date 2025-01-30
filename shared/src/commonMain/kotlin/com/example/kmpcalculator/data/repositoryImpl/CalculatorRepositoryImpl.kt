package com.example.kmpcalculator.data.repositoryImpl


import com.example.kmpcalculator.domain.model.CalculatorRequest
import com.example.kmpcalculator.domain.model.CalculatorResponse
import com.example.kmpcalculator.domain.repository.CalculatorRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class CalculatorRepositoryImpl(private val client: HttpClient) : CalculatorRepository {
    override suspend fun calculate(expression: String): Double {
        val response: HttpResponse = client.post("http://localhost:8080/calculate") {
            contentType(ContentType.Application.Json)
            setBody(CalculatorRequest(expression))
        }
        return response.body<CalculatorResponse>().result
    }
}