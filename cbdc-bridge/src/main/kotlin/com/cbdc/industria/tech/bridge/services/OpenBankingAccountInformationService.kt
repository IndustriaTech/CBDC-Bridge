package com.cbdc.industria.tech.bridge.services

import com.cbdc.industria.tech.bridge.data.CreateAccountAccessConsentResponseBody
import com.cbdc.industria.tech.bridge.data.GetAccountAccessConsentResponseBody
import com.cbdc.industria.tech.bridge.data.GetBankingEntityAccountResponseBody
import com.cbdc.industria.tech.bridge.data.GetPartyResponseBody
import com.cbdc.industria.tech.bridge.data.OpenBankingAccountAccessConsentCreationRequestBody
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken


@CordaService
class OpenBankingAccountInformationCordaService (private val serviceHub: AppServiceHub) : OpenBankingAccountInformationService(
    executor = Executors.newFixedThreadPool(THREADS_COUNT),
    host = HOST_URL
)

open class OpenBankingAccountInformationService(
    val executor: ExecutorService,
    val host: String
) : SingletonSerializeAsToken() {

    fun createAccountAccessConsent(
        xEnvId: Long,
        currencyId: Long,
        body: OpenBankingAccountAccessConsentCreationRequestBody
    ): Future<CreateAccountAccessConsentResponseBody> {
        val future = CompletableFuture<CreateAccountAccessConsentResponseBody>()

        executor.execute {
            val result = makePostRequest<CreateAccountAccessConsentResponseBody>(
                url = "$host/$OPEN_BANKING_ACCOUNT_INFORMATION/$ACCESS_CONSENTS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to currencyId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getAccount(
        xEnvId: Long,
        currencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long,
        accountId: Long
    ): Future<GetBankingEntityAccountResponseBody> {
        val future = CompletableFuture<GetBankingEntityAccountResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetBankingEntityAccountResponseBody>(
                url = "$host/$OPEN_BANKING_ACCOUNT_INFORMATION/$ACCOUNTS/$accountId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to currencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId,
                    X_CONSENT_ID to xConsentId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getAccountParty(
        xEnvId: Long,
        currencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long,
        accountId: Long
    ): Future<GetPartyResponseBody> {
        val future = CompletableFuture<GetPartyResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetPartyResponseBody>(
                url = "$host/$OPEN_BANKING_ACCOUNT_INFORMATION/$ACCOUNTS/$accountId/$PARTY",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to currencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId,
                    X_CONSENT_ID to xConsentId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getAccountAccessConsent(
        xEnvId: Long,
        currencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long
    ): Future<GetAccountAccessConsentResponseBody> {
        val future = CompletableFuture<GetAccountAccessConsentResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetAccountAccessConsentResponseBody>(
                url = "$host/$OPEN_BANKING_ACCOUNT_INFORMATION/$ACCESS_CONSENTS/$xConsentId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to currencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }
}
