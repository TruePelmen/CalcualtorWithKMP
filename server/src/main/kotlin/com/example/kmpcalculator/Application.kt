package com.example.kmpcalculator

import com.example.kmpcalculator.domain.model.CalculatorRequest
import com.example.kmpcalculator.domain.model.CalculatorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import javax.script.ScriptEngineManager
import io.ktor.serialization.kotlinx.json.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }
        .start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
    install(ContentNegotiation) { json() }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
        }
    }

    routing {
        get("/"){
            call.respondText { "Hello, dodik!" }
        }
        get("/test/{word}"){
            val name = call.parameters["word"]
            val header = call.request.headers["Connection"]
            call.respondText { "Darov, $name, with header: $header" }
        }
        post("/calculate") {
            val request = call.receive<CalculatorRequest>()
            val result = evaluateExpression(request.expression)
            call.respond(CalculatorResponse(result))
        }
        get("/calculate_get") {
            val num1 = call.request.queryParameters["num1"]?.toDoubleOrNull()
            val num2 = call.request.queryParameters["num2"]?.toDoubleOrNull()
            val operator = call.request.queryParameters["operator"]

            if (num1 == null || num2 == null || operator == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
                return@get
            }

            val result = calculate(num1, num2, operator)
            if (result != null) {
                call.respond(HttpStatusCode.OK, mapOf("result" to result))

            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid operator or division by zero")
            }
        }
    }
}

fun evaluateExpression(expression: String): Double {
    return try {
        val engine = ScriptEngineManager().getEngineByName("JavaScript")
        (engine.eval(expression) as Number).toDouble()
    } catch (e: Exception) {
        Double.NaN
    }
}

fun calculate(num1: Double, num2: Double, operator: String): Double? {
    return when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0.0) num1 / num2 else null
        else -> null
    }
}

@Serializable
data class CalculatorRequest(val expression: String)

@Serializable
data class CalculatorResponse(val result: Double)
