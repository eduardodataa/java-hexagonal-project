#!/bin/bash

# Script para testar mensagens SQS no sistema hexagonal
# Requer AWS CLI configurado ou LocalStack rodando

QUEUE_URL="http://localhost:4566/000000000000/order-commands"
EVENT_QUEUE_URL="http://localhost:4566/000000000000/order-events"

echo "🚀 Testando Sistema Hexagonal via SQS"
echo "====================================="

# Função para enviar comando CREATE_ORDER
test_create_order() {
    echo "📝 Testando CREATE_ORDER..."
    
    COMMAND_ID=$(uuidgen)
    CORRELATION_ID=$(uuidgen)
    
    MESSAGE='{
        "commandId": "'$COMMAND_ID'",
        "correlationId": "'$CORRELATION_ID'",
        "customerId": "customer123",
        "productId": "product456",
        "quantity": 2
    }'
    
    aws sqs send-message \
        --endpoint-url http://localhost:4566 \
        --queue-url "$QUEUE_URL" \
        --message-body "$MESSAGE" \
        --message-attributes CommandType=String,CREATE_ORDER
        
    echo "✅ Comando CREATE_ORDER enviado"
}

# Função para enviar comando UPDATE_ORDER_STATUS
test_update_status() {
    echo "📝 Testando UPDATE_ORDER_STATUS..."
    
    COMMAND_ID=$(uuidgen)
    CORRELATION_ID=$(uuidgen)
    ORDER_ID=$(uuidgen)
    
    MESSAGE='{
        "commandId": "'$COMMAND_ID'",
        "correlationId": "'$CORRELATION_ID'",
        "orderId": "'$ORDER_ID'",
        "status": "PROCESSING"
    }'
    
    aws sqs send-message \
        --endpoint-url http://localhost:4566 \
        --queue-url "$QUEUE_URL" \
        --message-body "$MESSAGE" \
        --message-attributes CommandType=String,UPDATE_ORDER_STATUS
        
    echo "✅ Comando UPDATE_ORDER_STATUS enviado"
}

# Função para enviar comando CANCEL_ORDER
test_cancel_order() {
    echo "📝 Testando CANCEL_ORDER..."
    
    COMMAND_ID=$(uuidgen)
    CORRELATION_ID=$(uuidgen)
    ORDER_ID=$(uuidgen)
    
    MESSAGE='{
        "commandId": "'$COMMAND_ID'",
        "correlationId": "'$CORRELATION_ID'",
        "orderId": "'$ORDER_ID'"
    }'
    
    aws sqs send-message \
        --endpoint-url http://localhost:4566 \
        --queue-url "$QUEUE_URL" \
        --message-body "$MESSAGE" \
        --message-attributes CommandType=String,CANCEL_ORDER
        
    echo "✅ Comando CANCEL_ORDER enviado"
}

# Função para verificar eventos
check_events() {
    echo "📨 Verificando eventos na fila order-events..."
    
    aws sqs receive-message \
        --endpoint-url http://localhost:4566 \
        --queue-url "$EVENT_QUEUE_URL" \
        --max-number-of-messages 10
}

# Menu principal
case "$1" in
    "create")
        test_create_order
        ;;
    "update")
        test_update_status
        ;;
    "cancel")
        test_cancel_order
        ;;
    "events")
        check_events
        ;;
    "all")
        test_create_order
        sleep 2
        test_update_status
        sleep 2
        test_cancel_order
        sleep 2
        check_events
        ;;
    *)
        echo "Uso: $0 {create|update|cancel|events|all}"
        echo ""
        echo "Comandos disponíveis:"
        echo "  create  - Enviar comando CREATE_ORDER"
        echo "  update  - Enviar comando UPDATE_ORDER_STATUS"
        echo "  cancel  - Enviar comando CANCEL_ORDER"
        echo "  events  - Verificar eventos na fila"
        echo "  all     - Executar todos os testes"
        exit 1
        ;;
esac

echo ""
echo "🎉 Teste concluído!"
echo "Verifique os logs da aplicação para ver o processamento das mensagens."
