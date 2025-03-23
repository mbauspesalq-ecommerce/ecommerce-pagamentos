package com.mbauspesalq.ecommerce.pagamentos.service

import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.feign.EcommerceEstoqueClient
import com.mbauspesalq.ecommerce.pagamentos.feign.ParceiroPagamentoClient
import com.mbauspesalq.ecommerce.pagamentos.feign.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.pagamentos.repository.PagamentoTransacaoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PagamentosService(
    private val repository: PagamentoTransacaoRepository,
    private val parceiroPagamentoClient: ParceiroPagamentoClient,
    private val ecommerceEstoqueClient: EcommerceEstoqueClient
) {
    fun novoPagamento(pagamentoRequest: PagamentoRequest): ResponseEntity<Any> {
        val produtosRequeridos = pagamentoRequest.produtos.map {
            ProdutoEstoqueRequest(it.id, it.quantidadeRequerida)
        }

        try {
            val estoqueResponse = ecommerceEstoqueClient.subtraiEstoque(produtosRequeridos)

            if (estoqueResponse.statusCode != HttpStatus.OK) {
                return ResponseEntity.unprocessableEntity().body("Estoque indisponível")
            }
        } catch (e: Exception) {
            return ResponseEntity.unprocessableEntity().body("Estoque indisponível")
        }

        val pagamentoTransacao = pagamentoRequest.toPagamentoTransacao(EstadoPagamento.CRIADO)

        val parceiroAutenticaPagamentoRequest = pagamentoTransacao.toParceiroAutenticaPagamentoRequest()

        try {
            val parceiroResponse = parceiroPagamentoClient.autenticaPagamento(parceiroAutenticaPagamentoRequest)

            if (parceiroResponse.statusCode == HttpStatus.CREATED) {
                pagamentoTransacao.estado = EstadoPagamento.APROVADO
            } else {
                pagamentoTransacao.estado = EstadoPagamento.NEGADO
                ecommerceEstoqueClient.devolveEstoque(produtosRequeridos)
            }
        } catch (e: Exception) {
            pagamentoTransacao.estado = EstadoPagamento.NEGADO
            ecommerceEstoqueClient.devolveEstoque(produtosRequeridos)
        }

        return ResponseEntity.ok(repository.adicionaTransacao(pagamentoTransacao)?.toResponse())
    }

    fun buscaPagamentosDoCliente(idCliente: String): List<PagamentoResponse> =
        repository.buscaTodosPeloIdCliente(idCliente).map { it.toResponse() }
}
