package com.mbauspesalq.ecommerce.pagamentos.controller

import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.service.PagamentosService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pagamentos")
class PagamentosController(
    private val service: PagamentosService
) {
    @PostMapping
    fun novoPagamento(@RequestBody pagamentoRequest: PagamentoRequest): ResponseEntity<Any> =
        service.novoPagamento(pagamentoRequest)


    @GetMapping("/{idCliente}")
    fun buscaPagamentosDoCliente(@PathVariable idCliente: String): ResponseEntity<List<PagamentoResponse>> =
        service.buscaPagamentosDoCliente(idCliente)
            .takeIf { it.isNotEmpty() }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}