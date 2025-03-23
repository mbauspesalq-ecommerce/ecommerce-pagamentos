package com.mbauspesalq.ecommerce.pagamentos.dto

data class PagamentoResponse(
    val idCliente: String,
    val idCarrinho: String,
    val produtos: List<ProdutoResumoResponse>,
    val pagamentoFinalizado: PagamentoFinalizado,
)

data class ProdutoResumoResponse(
    val id: Long,
    val quantidadeAdquirida: Int,
    val precoUnitario: Double,
)

data class PagamentoFinalizado(
    val valor: Double,
    val parcelas: Int,
    val estado: EstadoPagamento
)