package com.cbdc.industria.tech.bridge.views

import com.cbdc.industria.tech.bridge.data.BankingEntityWhereConsentGrantingPartyIsRegisteredRef
import com.cbdc.industria.tech.bridge.data.ConsentRequestingBankingEntityRef
import com.cbdc.industria.tech.bridge.data.ConsentGrantingPartyId
import com.cbdc.industria.tech.bridge.enums.AccessConsentStatus
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class AccountAccessConsentView(
    val accountAccessConsentId: Long,
    val status: AccessConsentStatus,
    val consentConsentGrantingPartyId: ConsentGrantingPartyId,
    val consentRequestingBankingEntityRef: ConsentRequestingBankingEntityRef,
    val bankingEntityWhereConsentGrantingPartyIsRegisteredRef: BankingEntityWhereConsentGrantingPartyIsRegisteredRef
)
