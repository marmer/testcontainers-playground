package io.github.marmer.testcontainersplayground

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("holidays")
class Holidays {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getHolidays() =
        RestTemplate().getForObject(
            "https://feiertage-api.de/api/?jahr=2021",
            HolidayMap::class.java
        )!!.toHolidays()
}

class HolidayMap : HashMap<String, HashMap<String, HashMap<String, String>>>() {
    fun toHolidays(): List<Holiday> =
        values.flatMap { holiday ->
            holiday.entries.map {
                Holiday(it.key, it.value.get("datum"))
            }
        }.distinct()
}

data class Holiday(val date: String, val name: String?)
