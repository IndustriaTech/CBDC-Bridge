package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.data.OpenAccountRequestBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyRequestBody
import com.cbdc.industria.tech.bridge.enums.PartyType
import com.cbdc.industria.tech.bridge.services.CommercialBankCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.getOrThrow

@StartableByRPC
class CreatePartyAndAccountBank(
    private val envId: Long,
    private val currencyId: Long,
    private val bankId: Long,
    private val partyFullLegalName: String,
    private val partyPostalAddress: String,
    private val partyType: PartyType
) : FlowLogic<PartyAndAccountIds>() {

    @Suspendable
    override fun call(): PartyAndAccountIds {
        val commercialBank = serviceHub.cordaService(CommercialBankCordaService::class.java)
        val partyId = commercialBank.registerPartyWithBank(
            xEnvId = envId,
            xCurrencyId = currencyId,
            bankId = bankId,
            body = RegisterPartyRequestBody(
                partyType = partyType,
                partyFullLegalName = partyFullLegalName,
                partyPostalAddress = partyPostalAddress
            )
        ).getOrThrow().data.id

        val accountId = commercialBank.openAccount(
            xEnvId = envId,
            xCurrencyId = currencyId,
            bankId = bankId,
            body = OpenAccountRequestBody(partyId)
        ).getOrThrow().data.id

        return PartyAndAccountIds(partyId, accountId)
    }
}
