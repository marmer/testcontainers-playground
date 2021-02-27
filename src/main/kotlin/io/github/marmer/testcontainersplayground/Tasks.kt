package io.github.marmer.testcontainersplayground

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@RestController
@RequestMapping("dbtasks")
class TasksApi(val taskRepository: TaskRepository) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks() =
        taskRepository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@RequestBody task: Task) {
        taskRepository.save(task)
    }
}

@Entity
data class Task(var description: String) {
    @Id
    val id: String = newUUID()
}

private fun newUUID() = UUID.randomUUID().toString()

interface TaskRepository : JpaRepository<Task, String>
