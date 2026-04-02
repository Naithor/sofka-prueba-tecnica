#!/bin/bash
# Script para inicializar el ambiente de desarrollo local

echo "🔧 Sofka Prueba Técnica - Setup Inicial"
echo "======================================="
echo ""

# 1. Validar Maven
echo "✓ Verificando Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Instálalo desde https://maven.apache.org"
    exit 1
fi
echo "✓ Maven OK"

# 2. Validar Java
echo "✓ Verificando Java 26..."
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Se requiere Java 26+"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]+')
echo "✓ Java $JAVA_VERSION OK"

# 3. Validar Docker (opcional)
echo "✓ Verificando Docker..."
if command -v docker &> /dev/null; then
    echo "✓ Docker detectado"
else
    echo "⚠️  Docker no detectado. Necesario para docker-compose"
fi

# 4. Build del proyecto
echo ""
echo "📦 Compilando proyecto..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Setup completado exitosamente!"
    echo ""
    echo "Próximos pasos:"
    echo "1. Local: Ejecutar mvn spring-boot:run en cada directorio de servicio"
    echo "2. Docker: Ejecutar docker-compose up -d --build"
    echo ""
    echo "URLs:"
    echo "- Clientes: http://localhost:8081/api/v1/clientes"
    echo "- Cuentas:  http://localhost:8082/api/v1/cuentas"
else
    echo "❌ Error en la compilación"
    exit 1
fi

