# Hexagonal Debit Service

Sistema de **débito automático PJ** implementado com arquitetura hexagonal (Ports & Adapters) utilizando Java 21, Spring Boot 3.3+ e **comunicação exclusiva via AWS SQS**.

## 🏗️ Arquitetura

Este projeto segue os princípios da **Arquitetura Hexagonal** (Ports & Adapters), separando claramente:

- **Domínio**: Regras de negócio puras (models, ports, services)
- **Infraestrutura**: Implementações técnicas (persistence, messaging)
- **Aplicação**: Orquestração e configuração

### Estrutura do Projeto

```
src/main/java/com/example/hexagonal/
├── domain/                    # Camada de Domínio
│   ├── model/               # Entidades de domínio
│   ├── port/                # Interfaces (Ports)
│   └── service/             # Casos de uso
├── infrastructure/          # Camada de Infraestrutura
│   ├── persistence/         # Adaptadores de persistência
│   ├── persistence/dynamodb/ # Adaptador DynamoDB (mock)
│   ├── messaging/           # Adaptadores de mensageria (SQS)
│   └── observability/       # Métricas e observabilidade
└── config/                  # Configurações
```

## 🚀 Tecnologias

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 (Amazon Corretto) | Linguagem principal |
| **Spring Boot** | 3.3+ | Framework base |
| **Spring Cloud AWS** | 3.1.0 | Integração AWS |
| **DynamoDB** | - | Banco NoSQL (mock inicial) |
| **AWS SQS** | - | **Comunicação exclusiva** |
| **Redis** | - | Cache distribuído |
| **Caffeine** | - | Cache local |
| **Datadog** | 1.7.0 | Observabilidade |
| **Gradle** | 8.10.2 | Build tool |
| **Docker** | Multi-arch | Containerização |

## 📊 Performance

**Capacidade**: **14 milhões de transações/mês**
- **~540 transações/segundo** (pico)
- **Thread pools otimizados** (200 threads máx)
- **Cache distribuído** (Redis + Caffeine)
- **Processamento assíncrono** (SQS)
- **Batch processing** (100 transações/lote)
- **Async processing** para alta throughput

## 📋 Pré-requisitos

- Java 21 (Amazon Corretto)
- Docker & Docker Compose
- AWS CLI (para deploy)
- Gradle 8.10.2+
- Redis (para cache)

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
docker buildx build --platform linux/amd64,linux/arm64 -t hexagonal-debit-service .
```

### Executar container
```bash
docker run -p 8080:8080 hexagonal-debit-service
```

## 📨 Comunicação via SQS

**Este sistema não possui API REST**. Toda comunicação é feita através de mensagens SQS:

### Filas SQS

| Fila | Tipo | Descrição |
|------|------|-----------|
| `debit-commands` | **Comandos** | Recebe comandos para processar |
| `debit-events` | **Eventos** | Publica eventos de domínio |

### Comandos (Entrada)

#### CREATE_DEBIT_TRANSACTION
```json
{
  "commandId": "uuid",
  "correlationId": "uuid",
  "companyId": "company123",
  "companyDocument": "12.345.678/0001-90",
  "companyName": "Empresa Exemplo LTDA",
  "bankAccountId": "account456",
  "amount": 1500.00,
  "description": "Débito automático mensal",
  "scheduledDate": "2024-01-15T10:00:00Z"
}
```

#### PROCESS_DEBIT_TRANSACTION
```json
{
  "commandId": "uuid",
  "correlationId": "uuid",
  "transactionId": "uuid"
}
```

#### RETRY_DEBIT_TRANSACTION
```json
{
  "commandId": "uuid",
  "correlationId": "uuid",
  "transactionId": "uuid"
}
```

#### CANCEL_DEBIT_TRANSACTION
```json
{
  "commandId": "uuid",
  "correlationId": "uuid",
  "transactionId": "uuid",
  "reason": "Solicitação do cliente"
}
```

### Eventos (Saída)

#### DEBIT_TRANSACTION_CREATED
```json
{
  "eventId": "uuid",
  "transactionId": "uuid",
  "eventType": "DEBIT_TRANSACTION_CREATED",
  "payload": "Debit transaction {id} created for company {companyId}",
  "timestamp": "2024-01-01T10:00:00Z",
  "correlationId": "uuid",
  "companyId": "company123"
}
```

#### DEBIT_TRANSACTION_PROCESSING
```json
{
  "eventId": "uuid",
  "transactionId": "uuid",
  "eventType": "DEBIT_TRANSACTION_PROCESSING",
  "payload": "Debit transaction {id} processing started",
  "timestamp": "2024-01-01T10:00:00Z",
  "correlationId": "uuid",
  "companyId": "company123"
}
```

#### DEBIT_TRANSACTION_RETRYING
```json
{
  "eventId": "uuid",
  "transactionId": "uuid",
  "eventType": "DEBIT_TRANSACTION_RETRYING",
  "payload": "Debit transaction {id} retry attempt {count}",
  "timestamp": "2024-01-01T10:00:00Z",
  "correlationId": "uuid",
  "companyId": "company123"
}
```

#### DEBIT_TRANSACTION_CANCELLED
```json
{
  "eventId": "uuid",
  "transactionId": "uuid",
  "eventType": "DEBIT_TRANSACTION_CANCELLED",
  "payload": "Debit transaction {id} cancelled: {reason}",
  "timestamp": "2024-01-01T10:00:00Z",
  "correlationId": "uuid",
  "companyId": "company123"
}
```

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `AWS_REGION` | Região AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | - |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | - |
| `SQS_QUEUE_NAME` | Fila de eventos | `debit-events` |
| `SQS_COMMAND_QUEUE_NAME` | Fila de comandos | `debit-commands` |
| `DYNAMODB_TABLE_NAME` | Tabela DynamoDB | `debit-transactions` |
| `REDIS_HOST` | Host Redis | `localhost` |
| `REDIS_PORT` | Porta Redis | `6379` |
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
- `debit.transactions.created`: Contador de transações criadas
- `debit.transactions.processed`: Contador de transações processadas
- `debit.transactions.processing.time`: Timer de processamento
- `debit.transactions.retry.count`: Contador de tentativas de retry

### Health Checks
- **Endpoint**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Thread Dump**: `/actuator/threaddump`
- **Heap Dump**: `/actuator/heapdump`

## 🚀 Deploy

### AWS Graviton (ARM64)

1. **Build para ARM64**:
```bash
docker buildx build --platform linux/arm64 -t hexagonal-debit-service:arm64 .
```

2. **Deploy em instâncias C7g/R7g**:
```bash
aws ecs create-service --cluster hexagonal-cluster --service-name debit-service
```

### Variáveis de Ambiente para Produção
```bash
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password
export AWS_REGION=us-east-1
export SQS_QUEUE_NAME=debit-events-prod
export SQS_COMMAND_QUEUE_NAME=debit-commands-prod
export DYNAMODB_TABLE_NAME=debit-transactions-prod
export REDIS_HOST=redis-cluster.prod
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

### Performance Monitoring
```bash
# Thread dump para análise de performance
curl http://localhost:8080/actuator/threaddump

# Heap dump para análise de memória
curl http://localhost:8080/actuator/heapdump
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

### Adicionando Novo Comando SQS
1. Crie DTO em `infrastructure/messaging/dto/`
2. Adicione handler em `DebitTransactionCommandListener`
3. Configure nova fila SQS

### DynamoDB Real
Para substituir o mock por DynamoDB real:
1. Configure credenciais AWS
2. Crie tabela DynamoDB
3. Implemente `DynamoDbRealAdapter`
4. Substitua o mock no Spring

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
