package com.cbdc.industria.tech.bridge.services

import com.cbdc.industria.tech.bridge.data.CreatePaymentInterfaceProviderRequestBody
import com.cbdc.industria.tech.bridge.data.CreatePaymentInterfaceProviderResponseBody
import com.cbdc.industria.tech.bridge.data.GetBankingEntityAccountResponseBody
import com.cbdc.industria.tech.bridge.data.GetBankingEntityAccountsPageResponseBody
import com.cbdc.industria.tech.bridge.data.GetPIPDetailsPageResponseBody
import com.cbdc.industria.tech.bridge.data.GetPIPDetailsResponseBody
import com.cbdc.industria.tech.bridge.data.GetPartyResponseBody
import com.cbdc.industria.tech.bridge.data.GetPartyViewsPageResponseBody
import com.cbdc.industria.tech.bridge.data.MakeDepositRequestBody
import com.cbdc.industria.tech.bridge.data.MakeWithdrawalRequestBody
import com.cbdc.industria.tech.bridge.data.OpenAccountRequestBody
import com.cbdc.industria.tech.bridge.data.OpenAccountResponseBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyRequestBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyResponseBody
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class PIPCordaService(private val serviceHub: AppServiceHub) : PIPService(
    executor = Executors.newFixedThreadPool(THREADS_COUNT),
    host = HOST_URL
)

open class PIPService(val executor: ExecutorService, val host: String) : SingletonSerializeAsToken() {

    fun getPIPs(
        xEnvId: Long,
        xCurrencyId: Long,
        pageIndex: Int = 0,
        pageSize: Int = 20
    ): Future<GetPIPDetailsPageResponseBody> {
        val future = CompletableFuture<GetPIPDetailsPageResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetPIPDetailsPageResponseBody>(
                url = "$host/$PIPS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                ),
                params = listOf(Pair(PAGE_INDEX_KEY, pageIndex), Pair(PAGE_SIZE_KEY, pageSize))
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun createPIP(
        xEnvId: Long,
        xCurrencyId: Long,
        body: CreatePaymentInterfaceProviderRequestBody
    ): Future<CreatePaymentInterfaceProviderResponseBody> {
        val future = CompletableFuture<CreatePaymentInterfaceProviderResponseBody>()

        executor.execute {
            val result = makePostRequest<CreatePaymentInterfaceProviderResponseBody>(
                url = "$host/$PIPS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getPIPParties(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        pageIndex: Int = 0,
        pageSize: Int = 20
    ): Future<GetPartyViewsPageResponseBody> {
        val future = CompletableFuture<GetPartyViewsPageResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetPartyViewsPageResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_PARTIES",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                ),
                params = listOf(Pair(PAGE_INDEX_KEY, pageIndex), Pair(PAGE_SIZE_KEY, pageSize))
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun registerPartyWithPIP(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        body: RegisterPartyRequestBody
    ): Future<RegisterPartyResponseBody> {
        val future = CompletableFuture<RegisterPartyResponseBody>()

        executor.execute {
            val result = makePostRequest<RegisterPartyResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_PARTIES",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getAccounts(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        pageIndex: Int = 0,
        pageSize: Int = 20
    ): Future<GetBankingEntityAccountsPageResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountsPageResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetBankingEntityAccountsPageResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                ),
                params = listOf(Pair(PAGE_INDEX_KEY, pageIndex), Pair(PAGE_SIZE_KEY, pageSize))
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun openAccount(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        body: OpenAccountRequestBody
    ): Future<OpenAccountResponseBody> {
        val future = CompletableFuture<OpenAccountResponseBody>()

        executor.execute {
            val result = makePostRequest<OpenAccountResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun withdrawalFromAccount(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        accountId: Long,
        body: MakeWithdrawalRequestBody
    ): Future<GetBankingEntityAccountResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountResponseBody>()

        executor.execute {
            val result = makePostRequest<GetBankingEntityAccountResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS/$accountId/$PIPS_ACCOUNTS_WITHDRAWAL",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun depositToAccount(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        accountId: Long,
        body: MakeDepositRequestBody
    ): Future<GetBankingEntityAccountResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountResponseBody>()

        executor.execute {
            val result = makePostRequest<GetBankingEntityAccountResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS/$accountId/$PIPS_ACCOUNTS_DEPOSIT",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getPIP(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long
    ): Future<GetPIPDetailsResponseBody> {
        val future = CompletableFuture<GetPIPDetailsResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetPIPDetailsResponseBody>(
                url = "$host/$PIPS/$pipId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun deletePIP(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long
    ): Future<GetPIPDetailsResponseBody> {
        val future = CompletableFuture<GetPIPDetailsResponseBody>()

        executor.execute {
            val result = makeDeleteRequest<GetPIPDetailsResponseBody>(
                url = "$host/$PIPS/$pipId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getPIPParty(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        partyId: Long
    ): Future<GetPartyResponseBody> {
        val future = CompletableFuture<GetPartyResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetPartyResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_PARTIES/$partyId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun deletePIPParty(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        partyId: Long
    ): Future<GetPartyResponseBody> {
        val future = CompletableFuture<GetPartyResponseBody>()

        executor.execute {
            val result = makeDeleteRequest<GetPartyResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_PARTIES/$partyId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getPIPAccount(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        accountId: Long
    ): Future<GetBankingEntityAccountResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetBankingEntityAccountResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS/$accountId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun deletePIPAccount(
        xEnvId: Long,
        xCurrencyId: Long,
        pipId: Long,
        accountId: Long
    ): Future<GetBankingEntityAccountResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountResponseBody>()

        executor.execute {
            val result = makeDeleteRequest<GetBankingEntityAccountResponseBody>(
                url = "$host/$PIPS/$pipId/$PIPS_ACCOUNTS/$accountId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }
}
