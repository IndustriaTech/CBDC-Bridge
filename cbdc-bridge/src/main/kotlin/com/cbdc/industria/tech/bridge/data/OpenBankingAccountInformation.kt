package com.cbdc.industria.tech.bridge.data

import com.cbdc.industria.tech.bridge.views.AccountAccessConsentView
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class OpenBankingAccountAccessConsentCreationRequestBody(
    val consentGrantingPartyId: ConsentGrantingPartyId,
    val bankingEntityWhereConsentGrantingPartyIsRegisteredRef: BankingEntityWhereConsentGrantingPartyIsRegisteredRef,
    val consentRequestingBankingEntityRef: ConsentRequestingBankingEntityRef
)

@CordaSerializable
data class CreateAccountAccessConsentResponseBody(
    val data: CreateAccountAccessConsentResponseBodyData
)

@CordaSerializable
data class CreateAccountAccessConsentResponseBodyData(
    val id: Long
)

@CordaSerializable
data class GetAccountAccessConsentResponseBody(val data: AccountAccessConsentView)
