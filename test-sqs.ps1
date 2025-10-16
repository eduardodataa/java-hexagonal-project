# Script PowerShell para testar mensagens SQS no sistema hexagonal
# Requer AWS CLI configurado ou LocalStack rodando

param(
    [Parameter(Position=0)]
    [ValidateSet("create", "update", "cancel", "events", "all")]
    [string]$Command = "help"
)

$QUEUE_URL = "http://localhost:4566/000000000000/order-commands"
$EVENT_QUEUE_URL = "http://localhost:4566/000000000000/order-events"

Write-Host "🚀 Testando Sistema Hexagonal via SQS" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

function Test-CreateOrder {
    Write-Host "📝 Testando CREATE_ORDER..." -ForegroundColor Yellow
    
    $COMMAND_ID = [System.Guid]::NewGuid().ToString()
    $CORRELATION_ID = [System.Guid]::NewGuid().ToString()
    
    $MESSAGE = @{
        commandId = $COMMAND_ID
        correlationId = $CORRELATION_ID
        customerId = "customer123"
        productId = "product456"
        quantity = 2
    } | ConvertTo-Json -Compress
    
    aws sqs send-message `
        --endpoint-url http://localhost:4566 `
        --queue-url $QUEUE_URL `
        --message-body $MESSAGE `
        --message-attributes CommandType=String,CREATE_ORDER
        
    Write-Host "✅ Comando CREATE_ORDER enviado" -ForegroundColor Green
}

function Test-UpdateStatus {
    Write-Host "📝 Testando UPDATE_ORDER_STATUS..." -ForegroundColor Yellow
    
    $COMMAND_ID = [System.Guid]::NewGuid().ToString()
    $CORRELATION_ID = [System.Guid]::NewGuid().ToString()
    $ORDER_ID = [System.Guid]::NewGuid().ToString()
    
    $MESSAGE = @{
        commandId = $COMMAND_ID
        correlationId = $CORRELATION_ID
        orderId = $ORDER_ID
        status = "PROCESSING"
    } | ConvertTo-Json -Compress
    
    aws sqs send-message `
        --endpoint-url http://localhost:4566 `
        --queue-url $QUEUE_URL `
        --message-body $MESSAGE `
        --message-attributes CommandType=String,UPDATE_ORDER_STATUS
        
    Write-Host "✅ Comando UPDATE_ORDER_STATUS enviado" -ForegroundColor Green
}

function Test-CancelOrder {
    Write-Host "📝 Testando CANCEL_ORDER..." -ForegroundColor Yellow
    
    $COMMAND_ID = [System.Guid]::NewGuid().ToString()
    $CORRELATION_ID = [System.Guid]::NewGuid().ToString()
    $ORDER_ID = [System.Guid]::NewGuid().ToString()
    
    $MESSAGE = @{
        commandId = $COMMAND_ID
        correlationId = $CORRELATION_ID
        orderId = $ORDER_ID
    } | ConvertTo-Json -Compress
    
    aws sqs send-message `
        --endpoint-url http://localhost:4566 `
        --queue-url $QUEUE_URL `
        --message-body $MESSAGE `
        --message-attributes CommandType=String,CANCEL_ORDER
        
    Write-Host "✅ Comando CANCEL_ORDER enviado" -ForegroundColor Green
}

function Check-Events {
    Write-Host "📨 Verificando eventos na fila order-events..." -ForegroundColor Yellow
    
    aws sqs receive-message `
        --endpoint-url http://localhost:4566 `
        --queue-url $EVENT_QUEUE_URL `
        --max-number-of-messages 10
}

# Menu principal
switch ($Command) {
    "create" {
        Test-CreateOrder
    }
    "update" {
        Test-UpdateStatus
    }
    "cancel" {
        Test-CancelOrder
    }
    "events" {
        Check-Events
    }
    "all" {
        Test-CreateOrder
        Start-Sleep -Seconds 2
        Test-UpdateStatus
        Start-Sleep -Seconds 2
        Test-CancelOrder
        Start-Sleep -Seconds 2
        Check-Events
    }
    default {
        Write-Host "Uso: .\test-sqs.ps1 {create|update|cancel|events|all}" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Comandos disponíveis:" -ForegroundColor Cyan
        Write-Host "  create  - Enviar comando CREATE_ORDER" -ForegroundColor White
        Write-Host "  update  - Enviar comando UPDATE_ORDER_STATUS" -ForegroundColor White
        Write-Host "  cancel  - Enviar comando CANCEL_ORDER" -ForegroundColor White
        Write-Host "  events  - Verificar eventos na fila" -ForegroundColor White
        Write-Host "  all     - Executar todos os testes" -ForegroundColor White
        exit 1
    }
}

Write-Host ""
Write-Host "🎉 Teste concluído!" -ForegroundColor Green
Write-Host "Verifique os logs da aplicação para ver o processamento das mensagens." -ForegroundColor Cyan
