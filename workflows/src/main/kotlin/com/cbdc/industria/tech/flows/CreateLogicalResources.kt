package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.data.CreateCommercialBankRequestBody
import com.cbdc.industria.tech.bridge.data.CreateCurrencyRequestBody
import com.cbdc.industria.tech.bridge.data.CreatePaymentInterfaceProviderRequestBody
import com.cbdc.industria.tech.bridge.enums.CurrencyCode
import com.cbdc.industria.tech.bridge.services.CommercialBankCordaService
import com.cbdc.industria.tech.bridge.services.CurrencyCordaService
import com.cbdc.industria.tech.bridge.services.PIPCordaService
import com.cbdc.industria.tech.bridge.services.SandboxEnvCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.getOrThrow

@StartableByRPC
/**
 * Create logical test environment
 * Create Currency
 * Create commercial banks
 * Create Payment Interface Providers (PIPs)
 */
class CreateLogicalResources(
    private val commercialBankName: String,
    private val pipName: String
) : FlowLogic<LogicalResources>() {

    @Suspendable
    override fun call(): LogicalResources {
        val envId = serviceHub.cordaService(SandboxEnvCordaService::class.java).postEnv().getOrThrow().data.id

        val currencyId = serviceHub.cordaService(CurrencyCordaService::class.java).postCurrency(
            xEnvId = envId,
            body = CreateCurrencyRequestBody(
                currencyCode = CurrencyCode.EUR
            )
        ).getOrThrow().data.id

        val commercialBankId = serviceHub.cordaService(CommercialBankCordaService::class.java)
            .createCommercialBank(
                xEnvId = envId,
                xCurrencyId = currencyId,
                body = CreateCommercialBankRequestBody(
                    commercialBankName = commercialBankName
                )
            ).getOrThrow().data.id

        val pipId = serviceHub.cordaService(PIPCordaService::class.java)
            .createPIP(
                xEnvId = envId,
                xCurrencyId = currencyId,
                body = CreatePaymentInterfaceProviderRequestBody(pipName = pipName)
            ).getOrThrow().data.id

        return LogicalResources(envId, currencyId, commercialBankId, pipId)
    }
}

@CordaSerializable
data class LogicalResources(
    val envId: Long,
    val currencyId: Long,
    val bankId: Long,
    val pipId: Long
)
