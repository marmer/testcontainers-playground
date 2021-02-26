package io.github.marmer.testcontainersplayground

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestcontainersPlaygroundApplication

fun main(args: Array<String>) {
    runApplication<TestcontainersPlaygroundApplication>(*args)
}
