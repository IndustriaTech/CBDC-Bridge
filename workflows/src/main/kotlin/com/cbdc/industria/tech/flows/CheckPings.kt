package com.cbdc.industria.tech.flows

import co.paralleluniverse.fibers.Suspendable
import com.cbdc.industria.tech.bridge.services.StartCordaService
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.getOrThrow

@StartableByRPC
class CheckPings : FlowLogic<List<String>>() {

    @Suspendable
    override fun call(): List<String> {
        val startService = serviceHub.cordaService(StartCordaService::class.java)

        val publicPing = startService.getPublicPing().getOrThrow().message
        val authPing = startService.getAuthPing().getOrThrow().message

        return listOf(publicPing, authPing)
    }
}
