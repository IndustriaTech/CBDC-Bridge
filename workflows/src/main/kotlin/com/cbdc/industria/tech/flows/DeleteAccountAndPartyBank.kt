package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.services.CommercialBankCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.getOrThrow

@StartableByRPC
class DeleteAccountAndPartyBank(
    private val accountAndPartyIds: PartyAndAccountIds,
    private val envId: Long,
    private val currencyId: Long,
    private val bankId: Long
) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val pipService = serviceHub.cordaService(CommercialBankCordaService::class.java)
        pipService.deleteAccount(envId, currencyId, bankId, accountAndPartyIds.accountId).getOrThrow()
        pipService.deleteParty(envId, currencyId, bankId, accountAndPartyIds.partyId).getOrThrow()
    }
}