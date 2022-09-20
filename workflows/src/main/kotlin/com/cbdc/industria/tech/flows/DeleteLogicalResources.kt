package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.services.CommercialBankCordaService
import com.cbdc.industria.tech.bridge.services.CurrencyCordaService
import com.cbdc.industria.tech.bridge.services.PIPCordaService
import com.cbdc.industria.tech.bridge.services.SandboxEnvCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.getOrThrow

@StartableByRPC
/**
 * Delete logical test environment
 * Delete Currency
 * Delete commercial banks
 * Delete Payment Interface Providers (PIPs)
 */
class DeleteLogicalResources(private val logicalResources: LogicalResources) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val envId = logicalResources.envId
        val currencyId = logicalResources.currencyId

        serviceHub.cordaService(PIPCordaService::class.java)
            .deletePIP(envId, currencyId, logicalResources.pipId).getOrThrow()
        serviceHub.cordaService(CommercialBankCordaService::class.java)
            .deleteCommercialBank(envId, currencyId, logicalResources.bankId).getOrThrow()
        serviceHub.cordaService(CurrencyCordaService::class.java)
            .deleteCurrency(envId, currencyId).getOrThrow()
        serviceHub.cordaService(SandboxEnvCordaService::class.java)
            .deleteEnv(envId).getOrThrow()
    }
}