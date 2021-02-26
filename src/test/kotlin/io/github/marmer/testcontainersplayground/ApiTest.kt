package io.github.marmer.testcontainersplayground

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
internal class ApiTest {
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
