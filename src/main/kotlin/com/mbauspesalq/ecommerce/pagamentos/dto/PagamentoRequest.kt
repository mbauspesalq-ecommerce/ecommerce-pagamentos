package com.mbauspesalq.ecommerce.pagamentos.dto

data class PagamentoRequest(
    val idCliente: String,
    val idCarrinho: String,
    val produtos: List<ProdutoResumoRequest>,
    val dadosPagamento: DadosPagamentoRequest,
)

data class ProdutoResumoRequest(
    val id: Long,
    val quantidadeRequerida: Int,
)

data class DadosPagamentoRequest(
    val tipo: Int,
    val parcelas: Int,
    val badge: String,
)
