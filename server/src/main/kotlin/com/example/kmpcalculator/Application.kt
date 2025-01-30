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
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
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
        post("/calculate") {
            val request = call.receive<CalculatorRequest>()
            val result = evaluateExpression(request.expression)
            call.respond(CalculatorResponse(result))
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

@Serializable
data class CalculatorRequest(val expression: String)

@Serializable
data class CalculatorResponse(val result: Double)
