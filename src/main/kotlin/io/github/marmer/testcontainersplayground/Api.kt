package io.github.marmer.testcontainersplayground

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("dbtasks")
class Api {

    private var currentTasks: List<Task> = emptyList()

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks() =
        currentTasks

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@RequestBody task: Task) {
        currentTasks += task
    }
}

data class Task(var description: String) {
    val id: String = newUUID()
}

private fun newUUID() = UUID.randomUUID().toString()
