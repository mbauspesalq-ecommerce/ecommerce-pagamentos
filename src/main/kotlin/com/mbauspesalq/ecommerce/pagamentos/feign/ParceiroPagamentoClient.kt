package com.mbauspesalq.ecommerce.pagamentos.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "parceiro-pagamento", url = "http://localhost:8083")
interface ParceiroPagamentoClient {
    @PostMapping("/parceiro/autentica-pagamento")
    fun autenticaPagamento(@RequestBody request: PagamentoRequest): ResponseEntity<ParceiroPagamentoResponse>
}

data class PagamentoRequest(
    val nomeParceiro: String,
    val valor: Double,
    val tipo: Int,
    val parcelas: Int,
    val badge: String
)

data class ParceiroPagamentoResponse(
    val mensagem: String?,
    val erro: String?
)