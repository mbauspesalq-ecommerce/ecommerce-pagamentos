package com.mbauspesalq.ecommerce.pagamentos.dto

data class PagamentoResponse(
    val idCliente: String,
    val idCarrinho: String,
    val valor: Double,
    val parcelas: Int,
    val estado: EstadoPagamento
)

