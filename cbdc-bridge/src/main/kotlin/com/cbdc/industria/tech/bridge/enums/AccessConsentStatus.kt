package com.cbdc.industria.tech.bridge.enums

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class AccessConsentStatus {
    AWAITING_AUTHORISATION, REJECTED, AUTHORISED, REVOKED
}