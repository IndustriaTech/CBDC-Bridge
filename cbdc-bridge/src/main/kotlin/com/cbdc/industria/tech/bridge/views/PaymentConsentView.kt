package com.cbdc.industria.tech.bridge.views

import com.cbdc.industria.tech.bridge.data.BankingEntityWhereConsentGrantingPartyIsRegisteredRef
import com.cbdc.industria.tech.bridge.data.PaymentInitiationDetails
import com.cbdc.industria.tech.bridge.data.ConsentRequestingBankingEntityRef
import com.cbdc.industria.tech.bridge.data.ConsentGrantingPartyId
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class PaymentConsentView(
    val paymentConsentId: String,
    val status: String,
    val consentConsentGrantingPartyId: ConsentGrantingPartyId,
    val consentRequestingBankingEntityRef: ConsentRequestingBankingEntityRef,
    val bankingEntityWhereConsentGrantingPartyIsRegisteredRef: BankingEntityWhereConsentGrantingPartyIsRegisteredRef,
    val paymentDetails: PaymentInitiationDetails
)
