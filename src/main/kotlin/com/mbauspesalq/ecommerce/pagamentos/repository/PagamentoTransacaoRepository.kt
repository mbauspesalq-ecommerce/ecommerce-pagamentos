package com.mbauspesalq.ecommerce.pagamentos.repository

import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.model.PagamentoTransacao
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest


@Repository
class PagamentoTransacaoRepository(
    private val dynamoDbTemplate: DynamoDbTemplate
) {

    fun adicionaTransacao(transacao: PagamentoTransacao): PagamentoTransacao? {
        dynamoDbTemplate.save(transacao)

        return transacao
    }

    fun buscaPeloIdClienteEIdCarrinho(idCliente: String, idCarrinho: String): PagamentoTransacao? {
        return dynamoDbTemplate.load(
            Key.builder()
                .partitionValue(idCliente)
                .sortValue(idCarrinho)
                .build(), PagamentoTransacao::class.java
        )
    }

    fun buscaTodosPeloIdCliente(idCliente: String): List<PagamentoTransacao> {
        val key = Key.builder().partitionValue(idCliente).build()

        val conditional = QueryConditional.keyEqualTo(key)

        return dynamoDbTemplate.query(
            QueryEnhancedRequest.builder()
                .queryConditional(conditional).build(),
            PagamentoTransacao::class.java
        ).items().toList()
    }

    fun atualizaEstado(idCliente: String, idCarrinho: String, estado: EstadoPagamento) {
        val transacao = buscaPeloIdClienteEIdCarrinho(idCliente, idCarrinho) ?: return
        transacao.estado = estado
        adicionaTransacao(transacao)
    }
}