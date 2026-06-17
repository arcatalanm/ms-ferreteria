#!/bin/bash

# FerreFix Launcher
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;36m                   INICIANDO FERREFIX LOCALLY\e[0m"
echo -e "\e[1;36m========================================================\e[0m"
echo "Para detener todo, puedes ejecutar: ./4-Stop-Locally.sh"
echo ""

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PIDS_FILE="$BASE_DIR/.services.pids"

# Limpiar archivo de PIDs anterior si existe
rm -f "$PIDS_FILE"

run_service() {
    local index=$1
    local name=$2
    local dir=$3
    
    echo -e "[$index/12] Levantando MS $name..."
    if [ -d "$BASE_DIR/$dir" ]; then
        cd "$BASE_DIR/$dir" || return
        # Asegurarse de que mvnw sea ejecutable
        if [ -f "mvnw" ]; then
            chmod +x mvnw
        fi
        
        # Crear carpeta para logs de consola si no existe
        mkdir -p logs
        
        # Ejecutar en segundo plano redireccionando stdout y stderr al archivo de log
        ./mvnw spring-boot:run > "logs/console.log" 2>&1 &
        local pid=$!
        echo "$pid" >> "$PIDS_FILE"
        echo "   -> [OK] $name iniciado con PID $pid (Logs en $dir/logs/console.log)"
    else
        echo -e "   -> \e[1;31m[ERROR]\e[0m El directorio $dir no existe"
    fi
}

run_service "1" "Auth" "ms_auth"
run_service "2" "Direcciones" "ms_direcciones"
run_service "3" "Inventario" "ms_inventario"
run_service "4" "Proveedores" "ms_proveedores"
run_service "5" "Usuarios" "ms_usuarios"
run_service "6" "Ventas" "ms_ventas"
run_service "7" "Marcas" "ms_marcas"
run_service "8" "Arriendo" "ms_arriendo"
run_service "9" "Compras" "ms_compras"
run_service "10" "Sugerencia" "ms_sugerencia"
run_service "11" "Reportes" "ms_reportes"

echo "Esperando arranque de microservicios (30 segundos)..."
sleep 30

run_service "12" "API Gateway" "api-gateway"

echo ""
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;32m  TODOS LOS SERVICIOS ESTAN ARRANCANDO EN SEGUNDO PLANO\e[0m"
echo -e "\e[1;32m  Tu API Gateway esta escuchando en http://localhost:8080\e[0m"
echo -e "\e[1;36m========================================================\e[0m"
echo ""
echo "Presiona Enter para finalizar este script (los servicios seguirán corriendo)..."
read -r
