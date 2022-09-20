package com.cbdc.industria.tech.bridge.services

import com.cbdc.industria.tech.bridge.data.CreateCurrencyRequestBody
import com.cbdc.industria.tech.bridge.data.CreateCurrencyResponseBody
import com.cbdc.industria.tech.bridge.data.GetCurrencyDetailsPageResponseBody
import com.cbdc.industria.tech.bridge.data.GetCurrencyDetailsResponseBody
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class CurrencyCordaService(val serviceHub: AppServiceHub) : CurrencyService(
    executor = Executors.newFixedThreadPool(THREADS_COUNT),
    host = HOST_URL
)

open class CurrencyService(
    private val executor: ExecutorService,
    private val host: String
) : SingletonSerializeAsToken() {


    fun getCurrencies(
        xEnvId: Long,
        pageIndex: Int = 0,
        pageSize: Int = 20
    ): Future<GetCurrencyDetailsPageResponseBody> {
        if (pageSize > MAX_PAGE_SIZE)
            throw IllegalArgumentException("pageSize must not be grater than $MAX_PAGE_SIZE.")

        val future = CompletableFuture<GetCurrencyDetailsPageResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetCurrencyDetailsPageResponseBody>(
                url = "$host/$CURRENCIES",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId
                ),
                params = listOf(Pair(PAGE_INDEX_KEY, pageIndex), Pair(PAGE_SIZE_KEY, pageSize))
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun postCurrency(
        xEnvId: Long,
        body: CreateCurrencyRequestBody
    ): Future<CreateCurrencyResponseBody> {
        val future = CompletableFuture<CreateCurrencyResponseBody>()

        executor.execute {
            val result = makePostRequest<CreateCurrencyResponseBody>(
                url = "$host/$CURRENCIES",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getCurrency(
        xEnvId: Long,
        currencyId: Long
    ): Future<GetCurrencyDetailsResponseBody> {
        val future = CompletableFuture<GetCurrencyDetailsResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetCurrencyDetailsResponseBody>(
                url = "$host/$CURRENCIES/$currencyId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun deleteCurrency(
        xEnvId: Long,
        currencyId: Long
    ): Future<GetCurrencyDetailsResponseBody> {
        val future = CompletableFuture<GetCurrencyDetailsResponseBody>()

        executor.execute {
            val result = makeDeleteRequest<GetCurrencyDetailsResponseBody>(
                url = "$host/$CURRENCIES/$currencyId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }
}
