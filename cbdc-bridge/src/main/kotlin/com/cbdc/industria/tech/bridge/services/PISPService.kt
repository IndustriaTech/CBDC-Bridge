package com.cbdc.industria.tech.bridge.services

import com.cbdc.industria.tech.bridge.data.CreatePaymentConsentResponseBody
import com.cbdc.industria.tech.bridge.data.GetDomesticPaymentDetailsResponseBody
import com.cbdc.industria.tech.bridge.data.MakeDomesticPaymentResponseBody
import com.cbdc.industria.tech.bridge.data.OpenBankingPaymentConsentCreationRequestBody
import com.cbdc.industria.tech.bridge.data.PaymentInitiationDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import net.corda.core.serialization.SingletonSerializeAsToken

open class PISPService(
    private val executor: ExecutorService,
    private val host: String
) : SingletonSerializeAsToken() {

    fun createDomesticPayment(
        xEnvId: Long,
        xCurrencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long,
        body: PaymentInitiationDetails
    ): Future<MakeDomesticPaymentResponseBody> {
        val future = CompletableFuture<MakeDomesticPaymentResponseBody>()

        executor.execute {
            val result = makePostRequest<MakeDomesticPaymentResponseBody>(
                url = "$host/$PISP/$DOMESTIC_PAYMENTS",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId,
                    X_CONSENT_ID to xConsentId,
                    CONTENT_TYPE to APPLICATION_JSON
                ),
                body = jacksonObjectMapper().writeValueAsBytes(body)
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun createDomesticPaymentConsent(
        xEnvId: Long,
        xCurrencyId: Long,
        body: OpenBankingPaymentConsentCreationRequestBody
    ): Future<CreatePaymentConsentResponseBody> {
        val future = CompletableFuture<CreatePaymentConsentResponseBody>()

        executor.execute {
            val result = makePostRequest<CreatePaymentConsentResponseBody>(
                url = "$host/$PISP/$DOMESTIC_PAYMENT_CONSENTS",
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

    fun getDomesticPayment(
        xEnvId: Long,
        xCurrencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long,
        domesticPaymentId: Long
    ): Future<GetDomesticPaymentDetailsResponseBody> {
        val future = CompletableFuture<GetDomesticPaymentDetailsResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetDomesticPaymentDetailsResponseBody>(
                url = "$host/$PISP/$DOMESTIC_PAYMENTS/$domesticPaymentId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId,
                    X_CONSENT_ID to xConsentId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }

    fun getDomesticPaymentConsent(
        xEnvId: Long,
        xCurrencyId: Long,
        xRequestingBankingEntityId: Long,
        xConsentId: Long
    ): Future<GetDomesticPaymentDetailsResponseBody> {
        val future = CompletableFuture<GetDomesticPaymentDetailsResponseBody>()

        executor.execute {
            val result = makeGetRequest<GetDomesticPaymentDetailsResponseBody>(
                url = "$host/$PISP/$DOMESTIC_PAYMENT_CONSENTS/$xConsentId",
                headers = mapOf(
                    AUTH_HEADER_KEY to AUTH_TOKEN,
                    X_ENV_ID to xEnvId,
                    X_CURRENCY_ID to xCurrencyId,
                    X_REQUESTING_BANKING_ENTITY_ID to xRequestingBankingEntityId
                )
            )
            result.toCompletableFuture(future)
        }

        return future
    }
}
