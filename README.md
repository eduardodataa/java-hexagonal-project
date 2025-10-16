# Hexagonal Order Service

Sistema de pedidos implementado com arquitetura hexagonal (Ports & Adapters) utilizando Java 21, Spring Boot 3.3+ e AWS SQS.

## 🏗️ Arquitetura

Este projeto segue os princípios da **Arquitetura Hexagonal** (Ports & Adapters), separando claramente:

- **Domínio**: Regras de negócio puras (models, ports, services)
- **Infraestrutura**: Implementações técnicas (persistence, messaging, api)
- **Aplicação**: Orquestração e configuração

### Estrutura do Projeto

```
src/main/java/com/example/hexagonal/
├── domain/                    # Camada de Domínio
│   ├── model/               # Entidades de domínio
│   ├── port/                # Interfaces (Ports)
│   └── service/             # Casos de uso
├── infrastructure/          # Camada de Infraestrutura
│   ├── api/                 # Adaptadores REST
│   ├── persistence/         # Adaptadores de persistência
│   ├── messaging/           # Adaptadores de mensageria
│   └── observability/       # Métricas e observabilidade
└── config/                  # Configurações
```

## 🚀 Tecnologias

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 (Amazon Corretto) | Linguagem principal |
| **Spring Boot** | 3.3+ | Framework base |
| **Spring Cloud AWS** | 3.1.0 | Integração AWS |
| **PostgreSQL** | 15+ | Banco de dados |
| **AWS SQS** | - | Mensageria |
| **Datadog** | 1.7.0 | Observabilidade |
| **Gradle** | 8.10.2 | Build tool |
| **Docker** | Multi-arch | Containerização |

## 📋 Pré-requisitos

- Java 21 (Amazon Corretto)
- Docker & Docker Compose
- AWS CLI (para deploy)
- Gradle 8.10.2+

## 🛠️ Configuração Local

### 1. Clone o repositório
```bash
git clone <repository-url>
cd java-hexagonal-project
```

### 2. Execute com Docker Compose
```bash
docker-compose up -d
```

### 3. Ou execute localmente
```bash
./gradlew bootRun
```

## 🧪 Testes

### Executar todos os testes
```bash
./gradlew test
```

### Executar testes com cobertura
```bash
./gradlew jacocoTestReport
```

### Executar PIT Mutation Testing
```bash
./gradlew pitest
```

### Cobertura de Testes
- **Meta**: > 90% de cobertura
- **JaCoCo**: Relatórios em `build/reports/jacoco/`
- **PIT**: Relatórios em `build/reports/pitest/`

## 🐳 Docker

### Build multi-arch
```bash
docker buildx build --platform linux/amd64,linux/arm64 -t hexagonal-order-service .
```

### Executar container
```bash
docker run -p 8080:8080 hexagonal-order-service
```

## 📡 API Endpoints

### Pedidos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/orders` | Criar pedido |
| `GET` | `/api/orders/{id}` | Buscar pedido por ID |
| `GET` | `/api/orders/customer/{customerId}` | Buscar pedidos por cliente |
| `GET` | `/api/orders/status/{status}` | Buscar pedidos por status |
| `PUT` | `/api/orders/{id}/status` | Atualizar status do pedido |
| `PUT` | `/api/orders/{id}/cancel` | Cancelar pedido |

### Exemplo de Criação de Pedido

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer123",
    "productId": "product456",
    "quantity": 2
  }'
```

## 📨 Contratos de Mensagens SQS

### Eventos de Pedido

#### ORDER_CREATED
```json
{
  "eventId": "uuid",
  "orderId": "uuid",
  "eventType": "ORDER_CREATED",
  "payload": "Order {id} created for customer {customerId}",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### ORDER_STATUS_UPDATED
```json
{
  "eventId": "uuid",
  "orderId": "uuid",
  "eventType": "ORDER_STATUS_UPDATED",
  "payload": "Order {id} status updated to {status}",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### ORDER_CANCELLED
```json
{
  "eventId": "uuid",
  "orderId": "uuid",
  "eventType": "ORDER_CANCELLED",
  "payload": "Order {id} cancelled",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |
| `AWS_REGION` | Região AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | - |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | - |
| `SQS_QUEUE_NAME` | Nome da fila SQS | `order-events` |
| `DD_ENV` | Ambiente Datadog | `development` |
| `DD_VERSION` | Versão Datadog | `1.0.0` |

### Profiles

- **default**: Configuração de produção
- **test**: Configuração para testes
- **docker**: Configuração para containers

## 📊 Observabilidade

### Datadog
- **Traces**: Rastreamento distribuído
- **Logs**: Centralização de logs
- **Métricas**: Métricas customizadas
- **APM**: Monitoramento de performance

### Métricas Customizadas
- `orders.created`: Contador de pedidos criados
- `orders.status.updated`: Contador de atualizações de status
- `orders.processing.time`: Timer de processamento

### Health Checks
- **Endpoint**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

## 🚀 Deploy

### AWS Graviton (ARM64)

1. **Build para ARM64**:
```bash
docker buildx build --platform linux/arm64 -t hexagonal-order-service:arm64 .
```

2. **Deploy em instâncias C7g/R7g**:
```bash
aws ecs create-service --cluster hexagonal-cluster --service-name order-service
```

### Variáveis de Ambiente para Produção
```bash
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password
export AWS_REGION=us-east-1
export DD_ENV=production
export DD_VERSION=1.0.0
```

## 🔍 Monitoramento

### Logs
```bash
docker logs -f <container-id>
```

### Métricas
```bash
curl http://localhost:8080/actuator/metrics
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🧩 Extensibilidade

### Adicionando Novo Port
1. Crie interface em `domain/port/`
2. Implemente em `infrastructure/`
3. Injete no service

### Adicionando Novo Adapter
1. Crie classe em `infrastructure/`
2. Implemente port correspondente
3. Configure bean no Spring

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou suporte, entre em contato através dos issues do repositório.
