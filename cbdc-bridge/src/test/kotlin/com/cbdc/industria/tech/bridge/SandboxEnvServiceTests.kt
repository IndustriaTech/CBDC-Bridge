package com.cbdc.industria.tech.bridge

import com.cbdc.industria.tech.bridge.services.HOST_URL
import com.cbdc.industria.tech.bridge.services.SandboxEnvService
import com.cbdc.industria.tech.bridge.services.THREADS_COUNT
import java.util.concurrent.Executors
import net.corda.core.utilities.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SandboxEnvServiceTests {
    private val sandboxEnvService = SandboxEnvService(
        executor = Executors.newFixedThreadPool(THREADS_COUNT),
        host = HOST_URL
    )

    private var environmentId: Long = 0

    @BeforeEach
    fun setup() {
        environmentId = sandboxEnvService.createEnvironment()
    }

    @AfterEach
    fun tearDown() {
        sandboxEnvService.deleteEnv(environmentId)
    }

    @Test
    fun `post environment`() {
        assertThat(sandboxEnvService.getEnvsWithoutTerminated().size).isNotEqualTo(0)
    }

    @Test
    fun `get environment`() {
        val getEnvFuture = sandboxEnvService.getEnv(environmentId)
        val getEnv = getEnvFuture.getOrThrow()
        assertThat(getEnv.data.id).isEqualTo(environmentId)
    }
}
