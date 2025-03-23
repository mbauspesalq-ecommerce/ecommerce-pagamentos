package com.mbauspesalq.ecommerce.pagamentos.service

import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.feign.ParceiroPagamentoClient
import com.mbauspesalq.ecommerce.pagamentos.repository.PagamentoTransacaoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class PagamentosService(
    private val repository: PagamentoTransacaoRepository,
    private val parceiroPagamentoClient: ParceiroPagamentoClient,
) {
    fun novoPagamento(pagamentoRequest: PagamentoRequest): PagamentoResponse? {
        val pagamentoTransacao = pagamentoRequest.toPagamentoTransacao(EstadoPagamento.CRIADO)
        val parceiroAutenticaPagamentoRequest = pagamentoTransacao.toParceiroAutenticaPagamentoRequest()

        try {
            val parceiroResponse = parceiroPagamentoClient.autenticaPagamento(parceiroAutenticaPagamentoRequest)

            if (parceiroResponse.statusCode == HttpStatus.CREATED) {
                pagamentoTransacao.estado = EstadoPagamento.APROVADO
            } else {
                pagamentoTransacao.estado = EstadoPagamento.NEGADO
            }
        } catch (e: Exception) {
            pagamentoTransacao.estado = EstadoPagamento.NEGADO
        }

        return repository.adicionaTransacao(pagamentoTransacao)?.toResponse()
    }

    fun buscaPagamentosDoCliente(idCliente: String): List<PagamentoResponse> =
        repository.buscaTodosPeloIdCliente(idCliente).map { it.toResponse() }
}
