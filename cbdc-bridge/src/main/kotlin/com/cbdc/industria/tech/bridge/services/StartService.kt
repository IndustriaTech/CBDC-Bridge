package com.cbdc.industria.tech.bridge.services

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.CordaSerializable
import net.corda.core.serialization.SerializationToken
import net.corda.core.serialization.SerializeAsToken
import net.corda.core.serialization.SerializeAsTokenContext
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class StartCordaService(val serviceHub: AppServiceHub) : StartService(
    executor = Executors.newFixedThreadPool(THREADS_COUNT),
    host = HOST_URL
)

open class StartService(
    private val executor: ExecutorService,
    private val host: String
) : SingletonSerializeAsToken() {

    fun getPublicPing(): Future<PingResponse> {
        val future = CompletableFuture<PingResponse>()

        executor.execute {
            val result = makeGetRequest<PingResponse>(url = "$host/start-here/public-ping")
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getAuthPing(): Future<PingResponse> {
        val future = CompletableFuture<PingResponse>()

        executor.execute {
            val result = makeGetRequest<PingResponse>(
                url = "$host/start-here/auth-ping",
                headers = mapOf(AUTH_HEADER_KEY to AUTH_TOKEN)
            )
            result.toCompletableFuture(future)
        }

        return future
    }
}

@CordaSerializable
data class PingResponse(val message: String)
