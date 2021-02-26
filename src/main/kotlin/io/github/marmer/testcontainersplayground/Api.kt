package io.github.marmer.testcontainersplayground

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("dbtasks")
class Api {
    @GetMapping
    fun getTasks() = listOf(Task("fancy Description1"), Task("fancy Description2"))

}

data class Task(val description: String, val id: String = newUUID())

private fun newUUID() = UUID.randomUUID().toString()
