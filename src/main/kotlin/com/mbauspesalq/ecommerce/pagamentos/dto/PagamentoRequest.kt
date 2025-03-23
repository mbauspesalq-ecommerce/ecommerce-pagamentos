package com.mbauspesalq.ecommerce.pagamentos.dto

import com.mbauspesalq.ecommerce.pagamentos.model.PagamentoTransacao

data class PagamentoRequest(
    val idCliente: String,
    val idCarrinho: String,
    val produtos: List<ProdutoResumoRequest>,
    val dadosPagamento: DadosPagamentoRequest,
) {
    data class ProdutoResumoRequest(
        val id: Long,
        val precoUnitario: Double,
        val quantidadeRequerida: Int,
    )

    data class DadosPagamentoRequest(
        val tipo: Int,
        val parcelas: Int,
        val badge: String,
    )

    fun toPagamentoTransacao(): PagamentoTransacao =
        PagamentoTransacao(
            idCliente = idCliente,
            idCarrinho = idCarrinho,
            valor = calculaValorTotal(),
            parcelas = dadosPagamento.parcelas,
            tipo = dadosPagamento.tipo,
            badge = dadosPagamento.badge
        )

    private fun calculaValorTotal(): Double = produtos.sumOf { it.precoUnitario * it.quantidadeRequerida }
}


