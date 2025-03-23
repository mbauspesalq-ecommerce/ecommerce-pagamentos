package com.mbauspesalq.ecommerce.pagamentos.repository

import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.model.PagamentoTransacao
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Repository
class PagamentoTransacaoRepository(
    private val dynamoDbClient: DynamoDbClient
) {
    private val enhancedClient: DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build()

    private val table =
        enhancedClient.table("ecommerce-pagamentos", TableSchema.fromBean(PagamentoTransacao::class.java))

    fun save(transacao: PagamentoTransacao) {
        table.putItem(transacao)
    }

    fun findById(idCliente: String, idCarrinho: String): PagamentoTransacao? {
        return table.getItem(
            Key.builder()
                .partitionValue(idCliente)
                .sortValue(idCarrinho)
                .build()
        )
    }

    fun updateEstado(idCliente: String, idCarrinho: String, estado: EstadoPagamento) {
        val transacao = findById(idCliente, idCarrinho) ?: return
        transacao.estado = estado
        save(transacao)
    }
}