package io.github.marmer.testcontainersplayground

import org.postgresql.ds.PGSimpleDataSource
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer

val postgresContainer = object : PostgreSQLContainer<Nothing>("postgres:13") {
    override fun start() {
        super.start()
        clearDb()
        setEnvironmentForSpring()
    }

    private fun setEnvironmentForSpring() {
        System.setProperty("spring.datasource.url", jdbcUrl)
        System.setProperty("spring.datasource.username", username)
        System.setProperty("spring.datasource.password", password)
    }

    private fun clearDb() {
        JdbcTemplate(PGSimpleDataSource().apply {
            setURL(jdbcUrl)
            password = super.getPassword()
            user = super.getUsername()
        }).execute("DROP TABLE IF EXISTS task,databasechangelog,databasechangeloglock CASCADE")
    }

    override fun stop() {
        // The JVM cares about the shutdown :)
    }
}.apply {
    withDatabaseName("someDb")
    withUsername("someUser")
    withPassword("somePw")
}
