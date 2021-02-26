package io.github.marmer.testcontainersplayground

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.temporal.ChronoUnit

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@Testcontainers
internal class GenericContainerIT {
    companion object {
        @Container
        val postgres = GenericContainer<Nothing>("postgres:13").apply {
            withExposedPorts(5432)

            withEnv("POSTGRES_USER", "someUser")
            withEnv("POSTGRES_PASSWORD", "somePw")
            withEnv("POSTGRES_DB", "someDb")
            setWaitStrategy(
                LogMessageWaitStrategy()
                    .withRegEx(".*database system is ready to accept connections.*\\s")
                    .withTimes(2)
                    .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS))
            )
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            //             Yeah. The changing ports allow parallelization
            registry.add("spring.datasource.url", { "jdbc:postgresql://localhost:${postgres.firstMappedPort}/someDb" });
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `speichern und laden von Elementen sollte klappen`() {
        // Preparation

        // Execution
        @Language("JSON") val content = "{\n  \"description\": \"fancy new task\"\n}"
        mockMvc.perform(
            post("/dbtasks").content(content)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)

        @Language("JSON") val expectedContent = "[\n  {\n    \"description\": \"fancy new task\"\n  }\n]"
        mockMvc.perform(get("/dbtasks"))
            // Assertion
            .andExpect(status().isOk)
            .andExpect(content().json(expectedContent))

    }

}
