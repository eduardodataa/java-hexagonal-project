# Arquitetura SOLID - Command Handler Pattern

## 🎯 Problema Resolvido

O código anterior violava o **Open-Closed Principle** do SOLID:

## ✅ Solução com Command Handler Pattern

### 1. **Single Responsibility Principle (SRP)**
Cada handler tem uma única responsabilidade:

```java
@Component
public class CreateDebitTransactionHandler extends CommandHandler<CreateDebitTransactionCommand> {
    // Responsabilidade única: criar transações de débito
}
```

### 2. **Open-Closed Principle (OCP)**
- ✅ **Aberto para extensão**: Novos handlers podem ser adicionados sem modificar código existente
- ✅ **Fechado para modificação**: Listener não precisa ser alterado

```java
// ✅ Para adicionar novo comando, apenas crie um novo handler
@Component
public class ValidateDebitTransactionHandler extends CommandHandler<CreateDebitTransactionCommand> {
    @Override
    public String getCommandType() {
        return "VALIDATE_DEBIT_TRANSACTION"; // Novo comando!
    }
    // ... implementação
}
```

### 3. **Liskov Substitution Principle (LSP)**
Todos os handlers podem ser substituídos pelo tipo base:

```java
CommandHandler<?> handler = registry.getHandler(commandType);
handler.processCommand(message); // Funciona para qualquer handler
```

### 4. **Interface Segregation Principle (ISP)**
Interface `CommandHandler` é específica e focada:

```java
public abstract class CommandHandler<T> {
    public abstract String getCommandType();
    public abstract Class<T> getCommandClass();
    public abstract void handle(T command);
    public void processCommand(String message) { /* template method */ }
}
```

### 5. **Dependency Inversion Principle (DIP)**
Listener depende de abstração (`CommandHandlerRegistry`), não de implementações concretas:

```java
@Component
public class DebitTransactionCommandListener {
    private final CommandHandlerRegistry commandHandlerRegistry; // Abstração
    
    public void handleDebitTransactionCommand(String message) {
        CommandHandler<?> handler = commandHandlerRegistry.getHandler(commandType);
        handler.processCommand(message); // Polimorfismo
    }
}
```

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    SQS Message                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│            DebitTransactionCommandListener                 │
│  - Recebe mensagem SQS                                     │
│  - Extrai tipo do comando                                  │
│  - Delega para registry                                    │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              CommandHandlerRegistry                         │
│  - Registra todos os handlers automaticamente             │
│  - Mapeia tipo de comando → handler                        │
│  - Implementa Strategy Pattern                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    CommandHandler<T>                       │
│  - Interface base (Template Method Pattern)               │
│  - processCommand() - template method                       │
│  - handle() - método abstrato                              │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   Create    │ │   Process   │ │    Retry    │
│   Handler   │ │   Handler   │ │   Handler   │
└─────────────┘ └─────────────┘ └─────────────┘
        │             │             │
        ▼             ▼             ▼
┌─────────────────────────────────────────────┐
│         DebitTransactionService             │
│         (Domain Service)                   │
└─────────────────────────────────────────────┘
```

## 🚀 Benefícios

### **Extensibilidade**
```java
// Para adicionar novo comando, apenas crie um novo handler:
@Component
public class NewCommandHandler extends CommandHandler<NewCommand> {
    @Override
    public String getCommandType() {
        return "NEW_COMMAND";
    }
    // Spring automaticamente registra!
}
```

### **Testabilidade**
```java
@Test
void shouldHandleNewCommand() {
    CommandHandler<?> handler = registry.getHandler("NEW_COMMAND");
    assertThat(handler).isNotNull();
    assertThat(handler.getCommandType()).isEqualTo("NEW_COMMAND");
}
```

### **Manutenibilidade**
- ✅ Cada handler é independente
- ✅ Fácil de testar isoladamente
- ✅ Fácil de modificar sem afetar outros
- ✅ Código mais limpo e organizado

### **Performance**
- ✅ Registry é criado uma vez na inicialização
- ✅ Lookup O(1) usando Map
- ✅ Sem reflexão em runtime
- ✅ Type safety com generics

## 📊 Comparação

| Aspecto | Antes (IFs) | Depois (Handlers) |
|---------|-------------|-------------------|
| **Extensibilidade** | ❌ Modificar listener | ✅ Novo handler |
| **Testabilidade** | ❌ Testar listener inteiro | ✅ Testar handler isolado |
| **Manutenibilidade** | ❌ Código acoplado | ✅ Código desacoplado |
| **SOLID** | ❌ Viola OCP | ✅ Segue todos os princípios |
| **Performance** | ❌ Múltiplos IFs | ✅ Lookup O(1) |

## 🎯 Conclusão

A refatoração demonstra como aplicar corretamente os princípios SOLID:

1. **SRP**: Cada handler tem uma responsabilidade
2. **OCP**: Aberto para extensão, fechado para modificação
3. **LSP**: Handlers são substituíveis
4. **ISP**: Interface específica e focada
5. **DIP**: Dependência de abstrações

**Resultado**: Código mais limpo, extensível, testável e manutenível! 🎉
