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
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@Testcontainers
internal class AlwaysFreshDbIT {
    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:13").apply {
            withDatabaseName("someDb")
            withUsername("someUser")
            withPassword("somePw")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            //             Yeah. The changing ports allow parallelization
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.username", postgres::getUsername);
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
