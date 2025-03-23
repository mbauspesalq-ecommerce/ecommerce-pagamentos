package com.mbauspesalq.ecommerce.pagamentos.controller

import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.service.PagamentosService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pagamentos")
class PagamentosController(
    private val service: PagamentosService
) {
    @PostMapping
    fun novoPagamento(@RequestBody pagamentoRequest: PagamentoRequest): ResponseEntity<PagamentoResponse> =
        service.novoPagamento(pagamentoRequest)
}