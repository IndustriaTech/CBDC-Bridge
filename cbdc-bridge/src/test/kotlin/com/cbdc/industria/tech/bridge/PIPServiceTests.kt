package com.cbdc.industria.tech.bridge

import com.cbdc.industria.tech.bridge.data.CreateCurrencyRequestBody
import com.cbdc.industria.tech.bridge.data.CreatePaymentInterfaceProviderRequestBody
import com.cbdc.industria.tech.bridge.data.MakeDepositRequestBody
import com.cbdc.industria.tech.bridge.data.MakeWithdrawalRequestBody
import com.cbdc.industria.tech.bridge.data.OpenAccountRequestBody
import com.cbdc.industria.tech.bridge.data.RegisterPartyRequestBody
import com.cbdc.industria.tech.bridge.enums.CurrencyCode
import com.cbdc.industria.tech.bridge.enums.PartyType
import com.cbdc.industria.tech.bridge.services.CommercialBankService
import com.cbdc.industria.tech.bridge.services.CurrencyService
import com.cbdc.industria.tech.bridge.services.HOST_URL
import com.cbdc.industria.tech.bridge.services.PIPService
import com.cbdc.industria.tech.bridge.services.SandboxEnvService
import com.cbdc.industria.tech.bridge.services.THREADS_COUNT
import java.util.concurrent.Executors
import net.corda.core.utilities.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class PIPServiceTests {
    private val pipService = PIPService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )
    private val currencyService = CurrencyService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )
    private val sandboxEnvService = SandboxEnvService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )
    private val commercialBankService = CommercialBankService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )

    var envId: Long = 0
    var currencyId: Long = 0
    private val defaultPipName: String = "raynors12"

    @BeforeAll
    fun createEnvAndCurrency() {
        envId = sandboxEnvService.postEnv().getOrThrow().data.id
        currencyId =
            currencyService.postCurrency(envId, CreateCurrencyRequestBody(CurrencyCode.EUR)).getOrThrow().data.id
    }

    @AfterAll
    fun deleteEnvAndCurrency() {
        currencyService.deleteCurrency(envId, currencyId).getOrThrow()
        sandboxEnvService.deleteEnv(envId).getOrThrow()
    }

    @Test
    fun createPIP() {
        val pipId = generatePIP()
        val pip = pipService.getPIP(envId, currencyId, pipId).getOrThrow().data
        assertThat(pip.id).isEqualTo(pipId)
        assertThat(pip.pipName).isEqualTo(defaultPipName)

        pipService.deletePIP(envId, currencyId, pipId).getOrThrow()
    }

    @Test
    fun registerPartyWithPIP() {
        val pipId = generatePIP()
        val partyId = registerParty(pipId)
        val party = pipService.getPIPParty(envId, currencyId, pipId, partyId).getOrThrow()

        assertThat(party.data.id).isEqualTo(partyId)

        pipService.deletePIPParty(envId, currencyId, pipId, partyId).getOrThrow()
        pipService.deletePIP(envId, currencyId, pipId).getOrThrow()
    }

    @Test
    fun openAccount() {
        val pipId = generatePIP()
        val partyId = registerParty(pipId)

        val body = OpenAccountRequestBody(partyId)
        val account = pipService.openAccount(envId, currencyId, pipId, body).getOrThrow()
        val getAccount = pipService.getPIPAccount(envId, currencyId, pipId, account.data.id).getOrThrow()

        assertThat(getAccount.data.id).isEqualTo(account.data.id)

        pipService.deletePIPAccount(envId, currencyId, pipId, account.data.id).getOrThrow()
        pipService.deletePIPParty(envId, currencyId, pipId, partyId).getOrThrow()
        pipService.deletePIP(envId, currencyId, pipId).getOrThrow()
    }

    @Test
    fun `deposit and withdrawal balance to account`() {
        val pipId = generatePIP()
        val partyId = registerParty(pipId)
        val body = OpenAccountRequestBody(partyId)
        val account = pipService.openAccount(envId, currencyId, pipId, body).getOrThrow()
        val accountDeposit = pipService.depositToAccount(
            envId, currencyId, pipId, account.data.id, MakeDepositRequestBody(200)
        ).getOrThrow()

        assertThat(accountDeposit.data.balance).isEqualTo(200)

        val accountWithdrawal = pipService.withdrawalFromAccount(
            envId, currencyId, pipId, account.data.id, MakeWithdrawalRequestBody(199)
        ).getOrThrow()

        assertThat(accountWithdrawal.data.balance).isEqualTo(1)

        pipService.deletePIPAccount(envId, currencyId, pipId, account.data.id).getOrThrow()
        pipService.deletePIPParty(envId, currencyId, pipId, partyId).getOrThrow()
        pipService.deletePIP(envId, currencyId, pipId).getOrThrow()
    }

    private fun registerParty(pipId: Long): Long {
        val registerPartyRequestBody = RegisterPartyRequestBody(
            partyType = PartyType.PRIVATE_LIMITED_COMPANY,
            partyFullLegalName = "Industria Technology",
            partyPostalAddress = "street 1"
        )
        val registerPartyResponse =
            pipService.registerPartyWithPIP(envId, currencyId, pipId, registerPartyRequestBody).getOrThrow()
        val partyId = registerPartyResponse.data.id
        return partyId
    }

    @Test
    fun getPips() {
        val pipId = generatePIP()
        val pipViews = pipService.getPIPs(envId, currencyId).getOrThrow().data.filter { it.status != "TERMINATED" }
        assertThat(pipViews.size).isEqualTo(1)
        assertThat(pipViews.first().id).isEqualTo(pipId)
        pipService.deletePIP(envId, currencyId, pipId).getOrThrow()
    }

    private fun generatePIP(pipName: String = defaultPipName): Long =
        pipService.createPIP(
            envId,
            currencyId,
            CreatePaymentInterfaceProviderRequestBody(pipName)
        ).getOrThrow().data.id

    @Ignore
    fun `delete all Accounts, Parties, PIPs, Currencies and Environments`() {
        val envs = sandboxEnvService.getEnvs().getOrThrow()
        val filteredEnv = envs.data.filter { it.status != "TERMINATED" }

        filteredEnv.forEach { env ->
            val currenciesForEnv = currencyService.getCurrencies(env.id)
                .getOrThrow().data.filter { currencyView ->
                    currencyView.status.toString() != "TERMINATED"
                        && env.currencies.map { it.currencyId }.contains(currencyView.id)
                }

            currenciesForEnv.forEach { currencyForEnv ->
                try {
                    println(" env id:  ${env.id}  currency id: ${currencyForEnv.id}")
                    val pips = pipService.getPIPs(env.id, currencyForEnv.id)
                        .getOrThrow().data.filter { pip ->
                            pip.status != "TERMINATED"
                                && currenciesForEnv.flatMap { currencyView ->
                                currencyView.pips.map { it.id }
                            }.contains(pip.id)
                        }

                    val comBanks = commercialBankService.getCommercialBanks(env.id, currencyForEnv.id)
                        .getOrThrow().data.filter { comBank ->
                            comBank.status.toString() != "TERMINATED" && currencyForEnv.commercialBanks.map { it.id }
                                .contains(comBank.id)
                        }

                    comBanks.forEach { comBank ->
                        val comAccounts =
                            commercialBankService.getAccounts(env.id, currencyForEnv.id, comBank.id).getOrThrow()
                                .data.filter { account ->
                                    account.status.toString() == "OPEN"
                                        && comBank.accounts.map { it.accountId }.contains(account.id)
                                }

                        val comParties =
                            commercialBankService.getParties(env.id, currencyForEnv.id, comBank.id).getOrThrow()
                                .data.filter { account ->
                                    account.status.toString() == "ACTIVE"
                                        && comBank.parties.map { it.partyId }.contains(account.id)
                                }

                        comAccounts.forEach {
                            commercialBankService.deleteAccount(
                                env.id,
                                currencyForEnv.id,
                                comBank.id,
                                it.id
                            ).getOrThrow()
                        }
                        comParties.forEach {
                            commercialBankService.deleteParty(
                                env.id,
                                currencyForEnv.id,
                                comBank.id,
                                it.id
                            ).getOrThrow()
                        }
                        commercialBankService.deleteCommercialBank(env.id, currencyForEnv.id, comBank.id).getOrThrow()
                    }

                    pips.forEach { pipView ->
                        pipView.accounts.forEach { account ->
                            if (account.status.toString() == "OPEN")
                                pipService.deletePIPAccount(env.id, currencyForEnv.id, pipView.id, account.accountId)
                                    .getOrThrow()
                        }
                        pipView.parties.forEach { party ->
                            if (party.status.toString() == "ACTIVE")
                                pipService.deletePIPParty(env.id, currencyForEnv.id, pipView.id, party.partyId)
                                    .getOrThrow()
                        }

                        pipService.deletePIP(env.id, currencyForEnv.id, pipView.id).getOrThrow()
                    }

                    currencyService.deleteCurrency(env.id, currencyForEnv.id).getOrThrow()
                    sandboxEnvService.deleteEnv(env.id).getOrThrow()
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }

//    @Test
//    fun openAccount() {
//        val response = pipService.openAccount(
//            301,
//            302,
//            303,
//            OpenAccountRequestBody(1)
//        ).getOrThrow()
//        println(response)
//    }
//
//    @Test
//    fun deletePIPAccount() {
//        pipService.deletePIPAccount(301, 302, 303, 0)
//    }
//
//    @Test
//    fun getAccounts() {
//        val accountsId = pipService.getAccounts(301, 302, 303).getOrThrow()
//        println(accountsId)
//    }
}