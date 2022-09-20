package com.cbdc.industria.tech

import java.time.Instant
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.NetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class MockNetworkSetup {
    lateinit var network: MockNetwork
    lateinit var paymentProvider: StartedMockNode
    private lateinit var commercialBank: StartedMockNode

    @BeforeEach
    fun setup() {
        network = MockNetwork(
            MockNetworkParameters(
                networkParameters = NetworkParameters(
                    minimumPlatformVersion = 10,
                    notaries = emptyList(),
                    maxMessageSize = 10485760,
                    maxTransactionSize = 10485760 * 50,
                    modifiedTime = Instant.now(),
                    epoch = 1,
                    whitelistedContractImplementations = emptyMap()
                ),
                cordappsForAllNodes = listOf(
                    TestCordapp.findCordapp("com.cbdc.industria.tech.contracts"),
                    TestCordapp.findCordapp("com.cbdc.industria.tech.flows"),
                    TestCordapp.findCordapp("com.cbdc.industria.tech.bridge")
                ),
                notarySpecs = listOf(
                    MockNetworkNotarySpec(
                        name = CordaX500Name.parse("O=Notary, L=London, C=GB"),
                        validating = false
                    )
                )
            )
        )
        paymentProvider = network.createPartyNode()
        commercialBank = network.createPartyNode()
        network.runNetwork()
    }

    @AfterEach
    fun tearDown() {
        network.stopNodes()
    }
}