package com.mbauspesalq.ecommerce.pagamentos.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.http.EcommerceEstoqueClient
import com.mbauspesalq.ecommerce.pagamentos.http.ParceiroPagamentoClient
import com.mbauspesalq.ecommerce.pagamentos.http.ProdutoEstoqueRequest
import com.mbauspesalq.ecommerce.pagamentos.model.PagamentoTransacao
import com.mbauspesalq.ecommerce.pagamentos.repository.PagamentoTransacaoRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class PagamentosServiceTest {

    private val repository: PagamentoTransacaoRepository = mock()
    private val parceiroPagamentoClient: ParceiroPagamentoClient = mock()
    private val ecommerceEstoqueClient: EcommerceEstoqueClient = mock()
    private val pagamentosService = PagamentosService(
        repository,
        parceiroPagamentoClient,
        ecommerceEstoqueClient
    )

    private val objectMapper = ObjectMapper()

    private lateinit var pagamentoRequest: PagamentoRequest
    private lateinit var pagamentoResponse: PagamentoResponse
    private lateinit var pagamentoTransacao: PagamentoTransacao

    @BeforeEach
    fun setup() {
        pagamentoRequest = PagamentoRequest(
            idCliente = "123456",
            idCarrinho = "654321",
            produtos = listOf(
                PagamentoRequest.ProdutoResumoRequest(
                    id = 1,
                    precoUnitario = 100.00,
                    quantidadeRequerida = 1
                ),
                PagamentoRequest.ProdutoResumoRequest(
                    id = 2,
                    precoUnitario = 200.00,
                    quantidadeRequerida = 3
                )
            ),
            dadosPagamento = PagamentoRequest.DadosPagamentoRequest(
                tipo = 1,
                parcelas = 2,
                simbolo = "VISA"
            )
        )

        pagamentoResponse = PagamentoResponse(
            idCliente = "123456",
            idCarrinho = "654321",
            valor = 700.00,
            parcelas = 2,
            estado = EstadoPagamento.APROVADO
        )

        pagamentoTransacao = PagamentoTransacao(
            idCliente = "123456",
            idCarrinho = "654321",
            estado = EstadoPagamento.APROVADO,
            valor = 700.00,
            parcelas = 2,
            tipo = 1,
            simbolo = "VISA"
        )
    }

    @Nested
    inner class NovoPagamentoTests {

        @Test
        fun `deve retornar status 200 e criar um pagamento com sucesso quando estoque disponível e pagamento aprovado`() {
            val produtosRequeridos = pagamentoRequest.produtos.map {
                ProdutoEstoqueRequest(it.id, it.quantidadeRequerida)
            }

            whenever(ecommerceEstoqueClient.subtraiEstoque(produtosRequeridos)).thenReturn(ResponseEntity.ok().build())
            whenever(parceiroPagamentoClient.autenticaPagamento(any())).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build())
            whenever(repository.adicionaTransacao(any())).thenReturn(pagamentoTransacao)

            val response = pagamentosService.novoPagamento(pagamentoRequest)

            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(700.00, (response.body as PagamentoResponse).valor)
            assertEquals(EstadoPagamento.APROVADO, (response.body as PagamentoResponse).estado)
        }

        @Test
        fun `deve retornar status 422 quando estoque indisponivel`() {
            val produtosRequeridos = pagamentoRequest.produtos.map {
                ProdutoEstoqueRequest(it.id, it.quantidadeRequerida)
            }

            whenever(ecommerceEstoqueClient.subtraiEstoque(produtosRequeridos)).thenReturn(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build())

            val response = pagamentosService.novoPagamento(pagamentoRequest)

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        }

        @Test
        fun `deve retornar status 403 quando falhar ao autenticar pagamento`() {
            val produtosRequeridos = pagamentoRequest.produtos.map {
                ProdutoEstoqueRequest(it.id, it.quantidadeRequerida)
            }

            whenever(ecommerceEstoqueClient.subtraiEstoque(produtosRequeridos)).thenReturn(ResponseEntity.ok().build())
            whenever(parceiroPagamentoClient.autenticaPagamento(any())).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())
            whenever(ecommerceEstoqueClient.devolveEstoque(produtosRequeridos)).thenReturn(ResponseEntity.ok().build())
            whenever(repository.adicionaTransacao(any())).thenReturn(pagamentoTransacao)

            val response = pagamentosService.novoPagamento(pagamentoRequest)

            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }
    }

    @Nested
    inner class BuscaPagamentosDoClienteTests {

        @Test
        fun `deve retornar lista de pagamentos quando existirem pagamentos para o cliente`() {
            whenever(repository.buscaTodosPeloIdCliente("123456")).thenReturn(listOf(pagamentoTransacao))

            val response = pagamentosService.buscaPagamentosDoCliente("123456")

            assertEquals(1, response.size)
            assertEquals("123456", response[0].idCliente)
            assertEquals(EstadoPagamento.APROVADO, response[0].estado)
        }

        @Test
        fun `deve retornar lista vazia quando não houver pagamentos para o cliente`() {
            whenever(repository.buscaTodosPeloIdCliente("123456")).thenReturn(emptyList())

            val response = pagamentosService.buscaPagamentosDoCliente("123456")

            assertEquals(0, response.size)
        }
    }
}