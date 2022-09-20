package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.data.OpenAccountRequestBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyRequestBody
import com.cbdc.industria.tech.bridge.enums.PartyType
import com.cbdc.industria.tech.bridge.services.PIPCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.getOrThrow

@StartableByRPC
class CreatePartyAndAccountPIP(
    private val envId: Long,
    private val currencyId: Long,
    private val pipId: Long,
    private val partyFullLegalName: String,
    private val partyPostalAddress: String,
    private val partyType: PartyType
) : FlowLogic<PartyAndAccountIds>() {

    @Suspendable
    override fun call(): PartyAndAccountIds {
        val pipService = serviceHub.cordaService(PIPCordaService::class.java)
        val partyId = pipService.registerPartyWithPIP(
            xEnvId = envId,
            xCurrencyId = currencyId,
            pipId = pipId,
            body = RegisterPartyRequestBody(
                partyType = partyType,
                partyFullLegalName = partyFullLegalName,
                partyPostalAddress = partyPostalAddress
            )
        ).getOrThrow().data.id

        val accountId = pipService.openAccount(
            xEnvId = envId,
            xCurrencyId = currencyId,
            pipId = pipId,
            body = OpenAccountRequestBody(partyId)
        ).getOrThrow().data.id

        return PartyAndAccountIds(partyId, accountId)
    }
}

@CordaSerializable
data class PartyAndAccountIds(val partyId: Long, val accountId: Long)
