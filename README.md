# ToolsChallenge - API de Pagamentos

Solução do desafio Tools Java Challenge (Time Elite) - API de Pagamentos.

## Tecnologias
- Java 17
- Spring Boot (web)
- Maven
- OpenAPI (springdoc) para documentação

## Endpoints
- `POST /api/payments` — criar pagamento
  - Body (JSON):
    ```json
    {
      "amount": 120.50,
      "cardNumber": "4111111111111112",
      "cardHolder": "Fulano da Silva",
      "expiry": "12/27",
      "cvv": "123",
      "paymentType": "AVISTA",
      "installments": 1
    }
    ```
  - Resposta (exemplo):
    ```json
    {
      "id": "uuid",
      "amount": 120.50,
      "status": "AUTORIZADO",
      "paymentMethod": {
        "type": "AVISTA",
        "cardNumberMasked": "**** **** **** 1112",
        "installments": 1
      },
      "createdAt": "2025-11-21T12:00:00Z"
    }
    ```

- `POST /api/payments/{id}/refund` — estornar por ID
  - Resposta:
    ```json
    {
      "id": "uuid",
      "refunded": true,
      "refundedAt": "2025-11-21T12:05:00Z",
      "message": "Estorno realizado com sucesso."
    }
    ```

- `GET /api/payments` — listar todos
- `GET /api/payments/{id}` — buscar por ID

## Regras de negócio
- `transaction.id` é UUID único.
- `status` é `AUTORIZADO` ou `NEGADO`.
- `paymentType` aceita `AVISTA`, `PARCELADO_LOJA`, `PARCELADO_EMISSOR`.

## Como rodar
1. Instalar JDK 17 e Maven.
2. Rodar:
   ```bash
   mvn clean package
   mvn spring-boot:run
