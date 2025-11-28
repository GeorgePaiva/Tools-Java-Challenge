# ToolsChallenge - API de Pagamentos

Solução do desafio Tools Java Challenge (Time Elite) - API de Pagamentos.

## Tecnologias
- Java 21
- Spring Boot (web)
- Maven
- OpenAPI (springdoc) para documentação

## Endpoints
- `POST /api/payments` — criar pagamento
  - Body (JSON):
    ```json
    {
      "transacao": {
          "cartao": "5275557453265890",
          "descricao": {
              "valor": "10000,00",
              "dataHora": "26/11/2025 11:59:07",
              "estabelecimento": "PetShop Mundo cão"
          },
          "formaPagamento": {
              "tipo": "AVISTA",
              "parcelas": "1"
          }
      }
    }
    ```
  - Resposta (exemplo):
    ```json
    {
      "transacao": {
          "cartao": "5275********5890",
          "descricao": {
              "codigoAutorizacao": "855059064",
              "dataHora": "25/11/2025 21:45:44",
              "estabelecimento": "PetShop Mundo cão",
              "nsu": "1213167555",
              "status": "AUTORIZADO",
              "valor": "0.01"
          },
          "formaPagamento": {
              "parcelas": "1",
              "tipo": "AVISTA"
          },
          "id": 1
      }
    }
    ```

- `POST /api/payments/{id}/refund` — estornar por ID
  - Resposta:
    ```json
    {
      "transacao": {
          "cartao": "5275********5890",
          "descricao": {
              "codigoAutorizacao": "290344673",
              "dataHora": "25/11/2025 21:44:00",
              "estabelecimento": "PetShop Mundo cão",
              "nsu": "6920652007",
              "status": "CANCELADO",
              "valor": "0.01"
          },
          "formaPagamento": {
              "parcelas": "1",
              "tipo": "AVISTA"
          },
          "id": 1
      }
    }
    ```

- `GET /api/payments` — listar todos
- `GET /api/payments/{id}` — buscar por ID

## Regras de negócio
- `transaction.id` é Long único.
- `status` é `AUTORIZADO`,`NEGADO` ou `CANCELADO` .
- `paymentType` aceita `AVISTA`, `PARCELADO_LOJA`, `PARCELADO_EMISSOR`.

## Como rodar
1. Instalar JDK 21 e Maven.
2. Rodar:
   ```bash
   mvn clean package
   mvn spring-boot:run

## Collection Postman
[Tools-Java-Challenge.postman_collection.json](https://github.com/user-attachments/files/23811990/Tools-Java-Challenge.postman_collection.json)
