package com.mbauspesalq.ecommerce.pagamentos.model

import com.mbauspesalq.ecommerce.pagamentos.annotation.NoArg
import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
@NoArg
data class PagamentoTransacao(

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("idCliente")
    var idCliente: String,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("idCarrinho")
    var idCarrinho: String,

    @get:DynamoDbAttribute("estado")
    var estado: EstadoPagamento = EstadoPagamento.CRIADO,

    @get:DynamoDbAttribute("valor")
    var valor: Double,

    @get:DynamoDbAttribute("parcelas")
    var parcelas: Int,

    @get:DynamoDbAttribute("tipo")
    var tipo: Int,

    @get:DynamoDbAttribute("badge")
    var badge: String
) {

    fun toResponse(): PagamentoResponse =
        PagamentoResponse(
            idCliente = idCliente,
            idCarrinho = idCarrinho,
            valor = valor,
            parcelas = parcelas,
            estado = estado
        )
}


