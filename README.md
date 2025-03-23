# Microsserviço ecommerce-pagamentos

## Objetivo

Este microsserviço foi desenvolvido como parte do Trabalho de Conclusão de Curso (TCC) do MBA em Engenharia de Software
da USP/Esalq. Ele exemplifica a aplicação da arquitetura de microsserviços no contexto de um e-commerce, fornecendo
funcionalidades relacionadas à gestão de pagamentos do e-commerce.

## Descrição

O microsserviço ecommerce-pagamentos responsável por:

- Receber e processar novos pagamentos, verificando estoque no microsserviço ecommerce-estoque e autenticando pagamento
  na API externa de parceiro, e guarda estado das transações no banco DynamoDB (NoSQL).
- Listar todo o histórico de transações dos clientes pelo idCliente que são armazenados no banco DynamoDB (NoSQL).

## Integrações

| Ação                                             | Integração Envolvida                                                        |
|--------------------------------------------------|-----------------------------------------------------------------------------|
| Verifica estoque de produtos disponíveis         | Microsserviço mbauspesalq-ecommerce-estoque `(api/estoque/subtrai-estoque)` |
| Autentica pagamento                              | API externa de parceiro (wiremock)                                          |
| Se pagamento falhar, devolve produtos ao estoque | Microsserviço mbauspesalq-ecommerce-estoque `(api/estoque/devolve-estoque)` |
| Registra Transação                               | Banco de Dados NoSQL DynamoDB (localstack)                                  |

## Endpoints

### 1. Criar um Novo Pagamento (POST /api/pagamentos)

#### Payload de Entrada

```json
{
  "idCliente": "123456",
  "idCarrinho": "654321",
  "produtos": [
    {
      "id": 1,
      "precoUnitario": 100.00,
      "quantidadeRequerida": 1
    },
    {
      "id": 2,
      "precoUnitario": 200.00,
      "quantidadeRequerida": 3
    }
  ],
  "dadosPagamento": {
    "tipo": 1,
    "parcelas": 2,
    "simbolo": "VISA"
  }
}
```

#### Fluxo do Processo

1. Verificação do Estoque
    - O serviço de pagamentos chama a API de Estoque (ecommerce-estoque) para verificar e subtrair a quantidade dos
      produtos.
    - Se a API retornar 422 (Unprocessable Entity), significa que o estoque está indisponível e a requisição falha.
    - Em caso de erro na comunicação com o estoque, o pagamento também falha.

2. Autenticação do Pagamento
    - Se o estoque foi reservado com sucesso, o serviço de pagamentos chama a API do Parceiro de Pagamento para
      autenticar a transação.
    - Se a API parceira retornar 201 (Created), o pagamento é APROVADO.
    - Se a API parceira negar o pagamento (400 Bad Request, 403 Forbidden, etc.), o serviço reverte a reserva no
      estoque.

3. Persistência da Transação
    - Independentemente do sucesso ou falha, a transação é salva no banco NoSQL (DynamoDB), com estado=APROVADO ou
      estado=NEGADO.

#### Respostas Possíveis

| Cenário              | Código HTTP              | Corpo da Resposta                                                                                       |
|----------------------|--------------------------|---------------------------------------------------------------------------------------------------------|
| Pagamento aprovado   | 200 OK                   | { "idCliente": "123456", "idCarrinho": "654321", "valor": 700.00, "parcelas": 2, "estado": "APROVADO" } |
| Estoque indisponível | 422 Unprocessable Entity | "Estoque indisponível"                                                                                  |
| Pagamento negado     | 403 Forbidden            | "Pagamento negado"                                                                                      |

### 2. Buscar Pagamentos de um Cliente (GET /api/pagamentos/{idCliente})

#### Parâmetro de Entrada

- idCLiente: String - ID do cliente cujas transações de pagamento devem ser buscadas.

#### Fluxo do Processo

1. O serviço consulta o repositório de pagamentos (DynamoDB) e retorna todas as transações associadas ao idCliente.
2. Se houver transações, a resposta contém a lista de pagamentos.
3. Se não houver transações, retorna um 404 Not Found.

#### Exemplo de Respostas

Caso existam pagamentos para o cliente (200 OK)

```json
[
  {
    "idCliente": "123456",
    "idCarrinho": "654321",
    "valor": 700.00,
    "parcelas": 2,
    "estado": "APROVADO"
  },
  {
    "idCliente": "123456",
    "idCarrinho": "111111",
    "valor": 4000.00,
    "parcelas": 10,
    "estado": "NEGADO"
  }
]
```

## Tecnologias Utilizadas

- Spring Boot
- Kotlin
- JVM 17
- Maven
- LocalStack
- AWS CLI
- Wiremock-gui

## Como Executar

### Requisitos

Antes de executar o microsserviço, certifique-se de ter instalado:

- Docker
- Java 17+
- Maven

### Executando todo o ecommerce com docker-compose

Para que todo o fluxo de pagamento seja validado, precisaremos subir o docker-compose com toda a infraestrutura do fluxo
de pagamento e estoque do ecommerce.

Para isso, deveremos baixar o docker-compose.yml do link: [TBD]

### Executando com Docker local

Obs.: para que o ecommerce funcione, você precisará subir a aplicação ecommerce-estoque na porta
8000: https://github.com/mbauspesalq-ecommerce/ecommerce-estoque.

1. Clone o repositório:

```bash
git clone https://github.com/mbauspesalq-ecommerce/ecommerce-pagamentos.git
cd ecommerce-pagamentos
```

2. Inicie os contêineres:

```bash
docker-compose up -d
```

3. Execute a aplicação:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```