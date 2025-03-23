package com.mbauspesalq.ecommerce.pagamentos.http

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "parceiro-pagamento", url = "http://localhost:8080")
interface ParceiroPagamentoClient {
    @PostMapping("/parceiro/autentica-pagamento")
    fun autenticaPagamento(@RequestBody request: ParceiroAutenticaPagamentoRequest): ResponseEntity<ParceiroPagamentoResponse>
}

data class ParceiroAutenticaPagamentoRequest(
    val nomeParceiro: String,
    val valor: Double,
    val tipo: Int,
    val parcelas: Int,
    val simbolo: String
)

data class ParceiroPagamentoResponse(
    val mensagem: String?,
    val erro: String?
)