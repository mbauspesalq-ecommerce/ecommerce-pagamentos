package com.mbauspesalq.ecommerce.pagamentos.model

import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
data class PagamentoTransacao(

    @get:DynamoDbPartitionKey
    var idCliente: String,

    @get:DynamoDbSortKey
    var idCarrinho: String,

    var produtos: List<ProdutoTransacao> = listOf(),

    var estado: EstadoPagamento,

    var valor: Double,

    var parcelas: Int
)

data class ProdutoTransacao(
    val id: Long,
    val preco: Double,
    val quantidadeRequerida: Int
)
