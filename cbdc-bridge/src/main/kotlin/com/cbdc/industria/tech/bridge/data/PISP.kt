package com.cbdc.industria.tech.bridge.data

import com.cbdc.industria.tech.bridge.views.DomesticPaymentView
import com.cbdc.industria.tech.bridge.views.PaymentConsentView
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class MakeDomesticPaymentResponseBody(
    val data: MakeDomesticPaymentResponseData
)

@CordaSerializable
data class MakeDomesticPaymentResponseData(val id: Long)

@CordaSerializable
data class OpenBankingPaymentConsentCreationRequestBody(
    val paymentDetails: PaymentInitiationDetails,
    val consentGrantingPartyId: ConsentGrantingPartyId,
    val consentRequestingBankingEntityRef: ConsentRequestingBankingEntityRef,
    val bankingEntityWhereConsentRequestingPartyIsRegisteredRef: BankingEntityWhereConsentGrantingPartyIsRegisteredRef
)

@CordaSerializable
data class PaymentInitiationDetails(
    val sourceAccountId: Long,
    val destinationAccountId: Long,
    val paymentAmountInCurrencyUnits: Long
)

@CordaSerializable
data class ConsentGrantingPartyId(val partyId: Long)

@CordaSerializable
data class ConsentRequestingBankingEntityRef(
    val bankingEntityType: String,
    val bankingEntityId: Long
)

@CordaSerializable
data class BankingEntityWhereConsentGrantingPartyIsRegisteredRef(
    val bankingEntityType: String,
    val bankingEntityId: Long
)

@CordaSerializable
data class CreatePaymentConsentResponseBody(
    val data: CreatePaymentConsentResponseBodyData
)

@CordaSerializable
data class CreatePaymentConsentResponseBodyData(
    val id: Long
)

@CordaSerializable
data class GetDomesticPaymentDetailsResponseBody(
    val data: DomesticPaymentView
)

@CordaSerializable
data class GetPaymentConsentResponseBody(
    val data: PaymentConsentView
)
