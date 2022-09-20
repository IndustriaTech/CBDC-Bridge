package com.cbdc.industria.tech.bridge.services

import com.cbdc.industria.tech.bridge.exceptions.CBDCBridgeException
import com.cbdc.industria.tech.bridge.exceptions.CBDCBridgeInternalServerError
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import net.corda.core.cordapp.CordappConfigException
import net.corda.core.node.AppServiceHub

val jacksonObjectMapper: ObjectMapper
    get() = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

inline fun <reified T : Any> makeGetRequest(
    url: String,
    headers: Map<String, Any>? = null,
    params: List<Pair<String, Any?>>? = null
): Result<T, FuelError> {

    val req = url.httpGet(params).header(headers).timeout(TIMEOUT_MILLISECONDS)
    return handleRequest(req)
}

inline fun <reified T : Any> makePostRequest(
    url: String,
    headers: Map<String, Any>? = null,
    params: List<Pair<String, Any?>>? = null,
    body: ByteArray? = null
): Result<T, FuelError> {

    val req = url.httpPost(params).header(headers).timeout(TIMEOUT_MILLISECONDS)
    if (body != null) req.body(body)
    return handleRequest(req)
}

inline fun <reified T : Any> makeDeleteRequest(
    url: String,
    headers: Map<String, Any>? = null,
    params: List<Pair<String, Any?>>? = null
): Result<T, FuelError> {

    val req = url.httpDelete(params).header(headers).timeout(TIMEOUT_MILLISECONDS)
    return handleRequest(req)
}

inline fun <reified T : Any> handleRequest(req: Request): Result<T, FuelError> {
    val (_, _, result) =
        if (T::class == String::class) {
            req.responseString()
        } else {
            req.responseObject(PaymentDeserializer<T>(mapper = jacksonObjectMapper))
        }

    return result as Result<T, FuelError>
}

inline fun <reified T : Any> Result<T, FuelError>.toCompletableFuture(future: CompletableFuture<T>) =
    fold(
        { obj -> future.complete(obj) },
        { err ->
            if (err.response.statusCode == 500)
                future.completeExceptionally(
                    CBDCBridgeInternalServerError(
                        statusCode = err.response.statusCode,
                        statusDescription = err.response.responseMessage,
                        message = String(err.response.data),
                        description = err.exception.message
                    )
                )
            else
                future.completeExceptionally(
                    CBDCBridgeException(
                        statusCode = err.response.statusCode,
                        message = String(err.response.data),
                        name = err.exception::class.java.name
                    )
                )
        }
    )

class PaymentDeserializer<T : Any> private constructor(
    private val clazz: KClass<out T>,
    private val mapper: ObjectMapper
) : ResponseDeserializable<T> {

    companion object {
        internal inline operator fun <reified T : Any> invoke(mapper: ObjectMapper): PaymentDeserializer<T> {
            return PaymentDeserializer(T::class, mapper)
        }
    }

    override fun deserialize(bytes: ByteArray): T {
        return mapper.readValue(bytes, clazz.java)
    }
}
