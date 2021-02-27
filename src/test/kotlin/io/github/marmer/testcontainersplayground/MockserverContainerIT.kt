package io.github.marmer.testcontainersplayground

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@Testcontainers
internal class MockserverContainerIT {
    companion object {
        @Container
        val mockServer: MockServerContainer =
            MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.11.2"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            //             Yeah. The changing ports allow parallelization
            registry.add("myConfig.holidayApiUrl", mockServer::getEndpoint);
        }


    }


    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `speichern und laden von Elementen sollte klappen`() {
        // Preparation
        MockServerClient(mockServer.getHost(), mockServer.getServerPort()).`when`(
            request()
                .withPath("/api")
                .withQueryStringParameter("jahr", "2021")
        )
            .respond(
                response()
                    .withBody("{\n  \"BW\": {\n    \"Neujahrstag\": {\n      \"datum\": \"2021-01-01\",\n      \"hinweis\": \"\"\n    },\n    \"Heilige Drei Könige\": {\n      \"datum\": \"2021-01-06\",\n      \"hinweis\": \"\"\n    }\n  }\n}")
            )

        // Execution
        @Language("JSON") val expectedContent =
            "[\n  {\n    \"date\": \"2021-01-01\",\n    \"name\": \"Neujahrstag\"\n  },\n  {\n    \"date\": \"2021-01-06\",\n    \"name\": \"Heilige Drei Könige\"\n  }\n]"
        mockMvc.perform(get("/holidays"))
            // Assertion
            .andExpect(status().isOk)
            .andExpect(content().json(expectedContent))

    }

}
