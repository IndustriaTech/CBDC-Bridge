package com.cbdc.industria.tech

import com.cbdc.industria.tech.bridge.enums.PartyType
import com.cbdc.industria.tech.flows.CheckPings
import com.cbdc.industria.tech.flows.CreateLogicalResources
import com.cbdc.industria.tech.flows.CreatePartyAndAccountBank
import com.cbdc.industria.tech.flows.CreatePartyAndAccountPIP
import com.cbdc.industria.tech.flows.DeleteLogicalResources
import com.cbdc.industria.tech.flows.DeleteAccountAndPartyBank
import com.cbdc.industria.tech.flows.DeleteAccountAndPartyPIP
import net.corda.core.utilities.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PreEventTests : MockNetworkSetup() {

    @Test
    fun `Test Connectivity to Hackathon APIs`() {
        val future = paymentProvider.startFlow(CheckPings())
        network.runNetwork()
        val pings = future.getOrThrow()

        assertThat(pings.size).isEqualTo(2)
        assertThat(pings[0]).isEqualTo("pong")
        assertThat(pings[1]).isEqualTo("pong with API key")
    }

    @Test
    fun `Create environment and logical resources`() {
        val createFuture = paymentProvider.startFlow(
            CreateLogicalResources(
                commercialBankName = "Bank of Bulgaria",
                pipName = "Easy-pay"
            )
        )
        network.runNetwork()
        val logicalResources = createFuture.getOrThrow()

        val deleteFuture = paymentProvider.startFlow(DeleteLogicalResources(logicalResources))
        network.runNetwork()
        deleteFuture.getOrThrow()
    }

    @Test
    fun `Onboard users and accounts at PIPs and commercial banks`() {
        val createFuture = paymentProvider.startFlow(
            CreateLogicalResources(
                commercialBankName = "Bank of Bulgaria",
                pipName = "Easy-pay"
            )
        )
        network.runNetwork()
        val logicalResources = createFuture.getOrThrow()

        val envId = logicalResources.envId
        val currencyId = logicalResources.currencyId
        val pipId = logicalResources.pipId
        val bankId = logicalResources.bankId

        val createAccountAndPartyPipFuture  = paymentProvider.startFlow(
            CreatePartyAndAccountPIP(
                envId = envId,
                currencyId = currencyId,
                pipId = pipId,
                partyFullLegalName = "Party Legal Name",
                partyPostalAddress = "street 1",
                partyType = PartyType.PUBLIC_LIMITED_COMPANY
            )
        )
        network.runNetwork()
        val accountAndPartyPip = createAccountAndPartyPipFuture.getOrThrow()

        val createAccountAndPartyBankFuture  = paymentProvider.startFlow(
            CreatePartyAndAccountBank(
                envId = envId,
                currencyId = currencyId,
                bankId = bankId,
                partyFullLegalName = "Party Legal Name",
                partyPostalAddress = "street 1",
                partyType = PartyType.PUBLIC_LIMITED_COMPANY
            )
        )
        network.runNetwork()
        val accountAndPartyBank = createAccountAndPartyBankFuture.getOrThrow()

        val deleteAccountAndPartyPipFuture = paymentProvider.startFlow(
            DeleteAccountAndPartyPIP(accountAndPartyPip, envId, currencyId, pipId)
        )
        network.runNetwork()
        deleteAccountAndPartyPipFuture.getOrThrow()

        val deleteAccountAndPartyBankFuture = paymentProvider.startFlow(
            DeleteAccountAndPartyBank(accountAndPartyBank, envId, currencyId, bankId)
        )
        network.runNetwork()
        deleteAccountAndPartyBankFuture.getOrThrow()

        val deleteFuture = paymentProvider.startFlow(DeleteLogicalResources(logicalResources))
        network.runNetwork()
        deleteFuture.getOrThrow()
    }
}
