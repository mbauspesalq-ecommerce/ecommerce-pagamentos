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
        val simbolo: String,
    )

    fun toPagamentoTransacao(estadoPagamento: EstadoPagamento): PagamentoTransacao =
        PagamentoTransacao(
            idCliente = idCliente,
            idCarrinho = idCarrinho,
            estado = estadoPagamento,
            valor = calculaValorTotal(),
            parcelas = dadosPagamento.parcelas,
            tipo = dadosPagamento.tipo,
            simbolo = dadosPagamento.simbolo
        )

    private fun calculaValorTotal(): Double = produtos.sumOf { it.precoUnitario * it.quantidadeRequerida }
}


