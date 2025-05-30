package com.mbauspesalq.ecommerce.pagamentos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class PagamentosApplication

fun main(args: Array<String>) {
	runApplication<PagamentosApplication>(*args)
}
