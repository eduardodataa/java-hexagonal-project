# Integração Datadog - Métricas e Observabilidade

## 🎯 Como a Aplicação Metrifica no Datadog

A aplicação está configurada para enviar métricas automaticamente para o Datadog através de múltiplas camadas de observabilidade.

## 🏗️ Arquitetura de Observabilidade

```
┌─────────────────────────────────────────────────────────────┐
│                    Aplicação                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Micrometer    │  │   Datadog       │  │   Spring    │ │
│  │   Metrics       │  │   Traces        │  │   Actuator  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Datadog Agent                            │
│  - Coleta métricas via StatsD                              │
│  - Coleta traces via APM                                   │
│  - Coleta logs via Log Agent                               │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Datadog Platform                         │
│  - Métricas: Dashboards, Alertas, Monitors                 │
│  - Traces: APM, Service Map, Performance                   │
│  - Logs: Centralizados, Correlacionados                     │
└─────────────────────────────────────────────────────────────┘
```

## 📊 Métricas Customizadas

### **1. Counters (Contadores)**

```java
@Component
public class DebitTransactionMetrics {
    
    @Trace("debit.transaction.created")
    public void recordDebitTransactionCreated() {
        debitTransactionCreatedCounter.increment();
    }
    
    @Trace("debit.transaction.processed")
    public void recordDebitTransactionProcessed() {
        debitTransactionProcessedCounter.increment();
    }
    
    @Trace("debit.transaction.failed")
    public void recordDebitTransactionFailed() {
        debitTransactionFailedCounter.increment();
    }
}
```

**Métricas enviadas para Datadog:**
- `debit.transactions.created` - Contador de transações criadas
- `debit.transactions.processed` - Contador de transações processadas
- `debit.transactions.failed` - Contador de transações falhadas
- `debit.transactions.retry` - Contador de tentativas de retry

### **2. Timers (Temporizadores)**

```java
@Trace("debit.transaction.processing.time")
public Timer.Sample startDebitTransactionProcessingTimer() {
    return Timer.start(debitTransactionProcessingTimer);
}

public void recordDebitTransactionProcessingTime(Timer.Sample sample) {
    sample.stop(); // Envia métricas de tempo para Datadog
}
```

**Métricas de tempo enviadas:**
- `debit.transactions.processing.time` - Tempo de processamento
- `debit.transactions.creation.time` - Tempo de criação

## 🔍 Traces Distribuídos

### **Anotações @Trace**

```java
@Trace("debit.transaction.created")
public void recordDebitTransactionCreated() {
    // Este método aparece como span no Datadog APM
}

@Trace("debit.transaction.processing.time")
public Timer.Sample startDebitTransactionProcessingTimer() {
    // Cria trace para monitoramento de performance
}
```

**Traces enviados para Datadog APM:**
- `debit.transaction.created`
- `debit.transaction.processed`
- `debit.transaction.failed`
- `debit.transaction.retry`
- `debit.transaction.processing.time`
- `debit.transaction.creation.time`

## ⚙️ Configuração

### **1. Dependências (build.gradle)**

```gradle
dependencies {
    implementation "com.datadoghq:dd-trace-api:${datadogVersion}"
    implementation "com.datadoghq:dd-trace-ot:${datadogVersion}"
    implementation "com.datadoghq:dd-java-agent:${datadogVersion}"
}
```

### **2. Configuração (application.yml)**

```yaml
datadog:
  trace:
    enabled: true
    service: hexagonal-debit-service
    env: ${DD_ENV:development}
    version: ${DD_VERSION:1.0.0}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,threaddump,heapdump
  metrics:
    export:
      prometheus:
        enabled: true
```

### **3. Beans de Métricas**

```java
@Configuration
public class ObservabilityConfig {
    
    @Bean
    public Counter debitTransactionCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("debit.transactions.created")
                .description("Number of debit transactions created")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
}
```

## 🚀 Como Funciona na Prática

### **1. Criação de Transação**

```java
@Override
public DebitTransaction createDebitTransaction(...) {
    Timer.Sample sample = debitTransactionMetrics.startDebitTransactionCreationTimer();
    
    try {
        // Lógica de negócio
        DebitTransaction transaction = // ... criar transação
        
        // Métricas
        debitTransactionMetrics.recordDebitTransactionCreated();
        
        return transaction;
    } finally {
        debitTransactionMetrics.recordDebitTransactionCreationTime(sample);
    }
}
```

**O que é enviado para Datadog:**
- ✅ **Counter**: `debit.transactions.created` +1
- ✅ **Timer**: `debit.transactions.creation.time` (duração)
- ✅ **Trace**: `debit.transaction.created` (span)
- ✅ **Tags**: `service=hexagonal-debit-service`

### **2. Processamento de Transação**

```java
@Override
public DebitTransaction processDebitTransaction(UUID transactionId) {
    // Lógica de negócio
    DebitTransaction transaction = // ... processar
    
    // Métricas
    debitTransactionMetrics.recordDebitTransactionProcessed();
    
    return transaction;
}
```

**O que é enviado para Datadog:**
- ✅ **Counter**: `debit.transactions.processed` +1
- ✅ **Trace**: `debit.transaction.processed` (span)

## 📈 Dashboards no Datadog

### **Métricas Disponíveis**

| Métrica | Tipo | Descrição |
|---------|------|-----------|
| `debit.transactions.created` | Counter | Transações criadas |
| `debit.transactions.processed` | Counter | Transações processadas |
| `debit.transactions.failed` | Counter | Transações falhadas |
| `debit.transactions.retry` | Counter | Tentativas de retry |
| `debit.transactions.processing.time` | Timer | Tempo de processamento |
| `debit.transactions.creation.time` | Timer | Tempo de criação |

### **Queries de Exemplo**

```sql
-- Taxa de sucesso
sum:debit.transactions.processed{service:hexagonal-debit-service} / 
sum:debit.transactions.created{service:hexagonal-debit-service} * 100

-- Tempo médio de processamento
avg:debit.transactions.processing.time{service:hexagonal-debit-service}

-- Taxa de erro
sum:debit.transactions.failed{service:hexagonal-debit-service} / 
sum:debit.transactions.created{service:hexagonal-debit-service} * 100
```

## 🔧 Configuração de Deploy

### **Variáveis de Ambiente**

```bash
# Datadog Agent
export DD_AGENT_HOST=datadog-agent
export DD_TRACE_AGENT_PORT=8126
export DD_LOGS_AGENT_PORT=10516

# Aplicação
export DD_ENV=production
export DD_VERSION=1.0.0
export DD_SERVICE=hexagonal-debit-service
export DD_TRACE_ENABLED=true
```

### **Docker Compose**

```yaml
services:
  app:
    environment:
      - DD_AGENT_HOST=datadog-agent
      - DD_ENV=production
      - DD_VERSION=1.0.0
      - DD_SERVICE=hexagonal-debit-service
    depends_on:
      - datadog-agent

  datadog-agent:
    image: datadog/agent:latest
    environment:
      - DD_API_KEY=${DD_API_KEY}
      - DD_SITE=datadoghq.com
      - DD_APM_ENABLED=true
      - DD_LOGS_ENABLED=true
```

## 📊 Monitoramento de Performance

### **Alertas Recomendados**

```yaml
# Taxa de erro alta
alert: debit_transaction_error_rate
condition: sum:debit.transactions.failed{service:hexagonal-debit-service} / 
           sum:debit.transactions.created{service:hexagonal-debit-service} > 0.05

# Tempo de processamento alto
alert: debit_transaction_slow_processing
condition: avg:debit.transactions.processing.time{service:hexagonal-debit-service} > 5000ms

# Volume de transações baixo
alert: debit_transaction_low_volume
condition: sum:debit.transactions.created{service:hexagonal-debit-service} < 1000
```

## 🎯 Benefícios

### **Observabilidade Completa**
- ✅ **Métricas**: Performance e volume
- ✅ **Traces**: Rastreamento distribuído
- ✅ **Logs**: Correlacionados com traces
- ✅ **APM**: Service map automático

### **Monitoramento Proativo**
- ✅ **Alertas**: Detecção precoce de problemas
- ✅ **Dashboards**: Visibilidade em tempo real
- ✅ **SLOs**: Service Level Objectives
- ✅ **Performance**: Otimização contínua

### **Debugging Facilitado**
- ✅ **Correlation ID**: Rastreamento end-to-end
- ✅ **Distributed Tracing**: Fluxo completo
- ✅ **Error Tracking**: Falhas identificadas rapidamente
- ✅ **Performance Profiling**: Bottlenecks identificados

## 🚀 Resultado Final

A aplicação envia automaticamente para o Datadog:

1. **Métricas customizadas** via Micrometer
2. **Traces distribuídos** via Datadog APM
3. **Logs estruturados** via Log Agent
4. **Health checks** via Spring Actuator
5. **Performance metrics** via JVM metrics

**Resultado**: Observabilidade completa e monitoramento proativo! 📊✨
