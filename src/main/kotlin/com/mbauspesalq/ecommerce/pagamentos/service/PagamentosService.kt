package com.mbauspesalq.ecommerce.pagamentos.service

import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.repository.PagamentoTransacaoRepository
import org.springframework.stereotype.Service

@Service
class PagamentosService(
    private val repository: PagamentoTransacaoRepository
) {
    fun novoPagamento(pagamentoRequest: PagamentoRequest): PagamentoResponse? =
        repository.adicionaTransacao(pagamentoRequest.toPagamentoTransacao())?.toResponse()


    fun buscaPagamentosDoCliente(idCliente: String): List<PagamentoResponse> =
        repository.buscaTodosPeloIdCliente(idCliente).map { it.toResponse() }
}
