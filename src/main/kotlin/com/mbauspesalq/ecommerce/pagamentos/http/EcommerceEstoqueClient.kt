package com.mbauspesalq.ecommerce.pagamentos.http

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "ecommerce-estoque", url = "http://ecommerce-estoque:8000")
interface EcommerceEstoqueClient {

    @PostMapping("/api/produtos/subtrai-estoque")
    fun subtraiEstoque(@RequestBody produtos: List<ProdutoEstoqueRequest>): ResponseEntity<String>

    @PostMapping("/api/produtos/devolve-estoque")
    fun devolveEstoque(@RequestBody produtos: List<ProdutoEstoqueRequest>): ResponseEntity<String>
}

data class ProdutoEstoqueRequest(
    val idProduto: Long,
    val quantidadeRequerida: Int
)