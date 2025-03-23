package com.mbauspesalq.ecommerce.pagamentos.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mbauspesalq.ecommerce.pagamentos.dto.EstadoPagamento
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoRequest
import com.mbauspesalq.ecommerce.pagamentos.dto.PagamentoResponse
import com.mbauspesalq.ecommerce.pagamentos.service.PagamentosService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PagamentosControllerTest {

    private val pagamentosService: PagamentosService = mock()
    private val pagamentosController: PagamentosController = PagamentosController(pagamentosService)

    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(pagamentosController).build()
    private val objectMapper = ObjectMapper()

    private lateinit var pagamentoRequest: PagamentoRequest
    private lateinit var pagamentoResponse: PagamentoResponse

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
    }

    @Nested
    inner class NovoPagamentoTests {
        @Test
        fun `deve criar um novo pagamento com sucesso e retornar status 200`() {
            whenever(pagamentosService.novoPagamento(any())).thenReturn(ResponseEntity.ok(pagamentoResponse))

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pagamentos")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(pagamentoRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.idCliente").value("123456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.estado").value("APROVADO"))
        }

        @Test
        fun `deve retornar status 422 quando Estoque esta indisponivel`() {
            whenever(pagamentosService.novoPagamento(any())).thenReturn(ResponseEntity.unprocessableEntity().body("Estoque indisponível"))

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pagamentos")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(pagamentoRequest))
            )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
        }

        @Test
        fun `deve retornar status 400 quando conteudo do body invalido`() {
            whenever(pagamentosService.novoPagamento(any())).thenThrow(RuntimeException("Exception não mapeada"))

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pagamentos")
                    .contentType("application/json")
                    .content("invalid")
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Nested
    inner class BuscaPagamentosDoClienteTestes {
        @Test
        fun `deve retornar status 200 com lista de pagamentos ja feitos pelo cliente`() {
            whenever(pagamentosService.buscaPagamentosDoCliente("123456")).thenReturn(listOf(pagamentoResponse))

            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pagamentos/123456")
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idCliente").value("123456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].estado").value("APROVADO"))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
        }

        @Test
        fun `deve retornar status 404 quando nao houver pagamentos para o cliente`() {
            whenever(pagamentosService.buscaPagamentosDoCliente("123456")).thenReturn(emptyList())

            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pagamentos/123456")
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }
}