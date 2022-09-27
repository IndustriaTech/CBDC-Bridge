package com.cbdc.industria.tech.bridge

import com.cbdc.industria.tech.bridge.data.MakeDepositRequestBody
import com.cbdc.industria.tech.bridge.data.MakeWithdrawalRequestBody
import com.cbdc.industria.tech.bridge.data.OpenAccountRequestBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyRequestBody
import com.cbdc.industria.tech.bridge.enums.CurrencyCode
import com.cbdc.industria.tech.bridge.enums.PartyType
import com.cbdc.industria.tech.bridge.services.CommercialBankService
import com.cbdc.industria.tech.bridge.services.CurrencyService
import com.cbdc.industria.tech.bridge.services.HOST_URL
import com.cbdc.industria.tech.bridge.services.SandboxEnvService
import com.cbdc.industria.tech.bridge.services.THREADS_COUNT
import java.util.concurrent.Executors
import net.corda.core.utilities.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommercialBankServiceTests {

    private val sandboxEnvService = SandboxEnvService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )

    private val currenciesService = CurrencyService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )

    private val commercialBankService = CommercialBankService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )

    private var environmentId: Long = 0
    private var currencyId: Long = 0
    private var commercialBankId: Long = 0

    @BeforeEach
    fun setup() {
        environmentId = sandboxEnvService.createEnvironment()
        currencyId = currenciesService.createCurrency(CurrencyCode.EUR, environmentId)
        commercialBankId = commercialBankService.createCommercialBank(
            environmentId = environmentId,
            currencyId = currencyId
        )
    }

    @AfterEach
    fun tearDown() {
        commercialBankService.deleteCommercialBank(xEnvId = environmentId, xCurrencyId = currencyId, bankId = commercialBankId).getOrThrow()
        currenciesService.deleteCurrency(xEnvId = environmentId, currencyId = currencyId).getOrThrow()
        sandboxEnvService.deleteEnv(environmentId).getOrThrow()
    }

    @Test
    fun `create commercial bank`() {
        assertThat(
            commercialBankService.getCommercialBanks(
                xEnvId = environmentId,
                xCurrencyId = currencyId
            ).getOrThrow().data.size
        )
            .isEqualTo(1)
    }

    @Test
    fun `get commercial bank`() {
        assertThat(
            commercialBankService.getCommercialBank(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId
            ).getOrThrow().data.id
        )
            .isEqualTo(commercialBankId)
    }

    @Test
    fun `create commercial bank party`() {
        val partyId = commercialBankService.registerPartyWithBank(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = RegisterPartyRequestBody(
                partyType = PartyType.INDIVIDUAL,
                partyFullLegalName = "O=PartyA,L=London,C=GB",
                partyPostalAddress = "Party Address"
            )
        ).getOrThrow().data.id

        assertThat(
            commercialBankService.getParties(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId
            ).getOrThrow().data.size
        )
            .isEqualTo(1)

        commercialBankService.deleteParty(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            partyId = partyId
        ).getOrThrow()
    }

    @Test
    fun `get commercial bank party`() {
        val partyId = commercialBankService.registerPartyWithBank(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = RegisterPartyRequestBody(
                partyType = PartyType.INDIVIDUAL,
                partyFullLegalName = "O=PartyA,L=London,C=GB",
                partyPostalAddress = "Party Address"
            )
        ).getOrThrow().data.id

        assertThat(
            commercialBankService.getParty(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId,
                partyId = partyId
            ).getOrThrow().data.id
        )
            .isEqualTo(partyId)

        commercialBankService.deleteParty(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            partyId = partyId
        ).getOrThrow()
    }

    @Test
    fun `create account for party in commercial bank`() {
        val partyId = commercialBankService.registerPartyWithBank(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = RegisterPartyRequestBody(
                partyType = PartyType.INDIVIDUAL,
                partyFullLegalName = "O=PartyA,L=London,C=GB",
                partyPostalAddress = "Party Address"
            )
        ).getOrThrow().data.id

        val accountId = commercialBankService.openAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = OpenAccountRequestBody(partyId)
        ).getOrThrow().data.id

        assertThat(
            commercialBankService.getAccounts(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId
            ).getOrThrow().data.size
        )
            .isEqualTo(1)

        commercialBankService.deleteAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId
        ).getOrThrow()

        commercialBankService.deleteParty(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            partyId = partyId
        ).getOrThrow()
    }

    @Test
    fun `make account deposit`() {
        val partyId = commercialBankService.registerPartyWithBank(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = RegisterPartyRequestBody(
                partyType = PartyType.INDIVIDUAL,
                partyFullLegalName = "O=PartyA,L=London,C=GB",
                partyPostalAddress = "Party Address"
            )
        ).getOrThrow().data.id

        val accountId = commercialBankService.openAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = OpenAccountRequestBody(partyId)
        ).getOrThrow().data.id

        commercialBankService.deposit(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId,
            body = MakeDepositRequestBody(100)
        ).getOrThrow()

        assertThat(
            commercialBankService.getAccount(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId,
                accountId = accountId
            ).getOrThrow().data.balance
        )
            .isEqualTo(100)

        commercialBankService.withdrawalAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId,
            body = MakeWithdrawalRequestBody(100)
        ).getOrThrow()

        commercialBankService.deleteAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId
        ).getOrThrow()

        commercialBankService.deleteParty(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            partyId = partyId
        ).getOrThrow()
    }

    @Test
    fun `make account withdraw`() {
        val partyId = commercialBankService.registerPartyWithBank(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = RegisterPartyRequestBody(
                partyType = PartyType.INDIVIDUAL,
                partyFullLegalName = "O=PartyA,L=London,C=GB",
                partyPostalAddress = "Party Address"
            )
        ).getOrThrow().data.id

        val accountId = commercialBankService.openAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            body = OpenAccountRequestBody(partyId)
        ).getOrThrow().data.id

        commercialBankService.deposit(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId,
            body = MakeDepositRequestBody(100)
        ).getOrThrow()

        assertThat(
            commercialBankService.getAccount(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId,
                accountId = accountId
            ).getOrThrow().data.balance
        )
            .isEqualTo(100)

        commercialBankService.withdrawalAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId,
            body = MakeWithdrawalRequestBody(100)
        ).getOrThrow()

        assertThat(
            commercialBankService.getAccount(
                xEnvId = environmentId,
                xCurrencyId = currencyId,
                bankId = commercialBankId,
                accountId = accountId
            ).getOrThrow().data.balance
        )
            .isEqualTo(0)

        commercialBankService.deleteAccount(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            accountId = accountId
        ).getOrThrow()

        commercialBankService.deleteParty(
            xEnvId = environmentId,
            xCurrencyId = currencyId,
            bankId = commercialBankId,
            partyId = partyId
        ).getOrThrow()
    }
}