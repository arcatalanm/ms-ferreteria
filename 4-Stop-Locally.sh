#!/bin/bash

# FerreFix Stopper
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;36m                 DETENIENDO FERREFIX LOCALLY\e[0m"
echo -e "\e[1;36m========================================================\e[0m"
echo ""

BASE_DIR="$(pwd)"
PIDS_FILE="$BASE_DIR/.services.pids"

if [ -f "$PIDS_FILE" ]; then
    echo "Deteniendo microservicios registrados en .services.pids..."
    while IFS= read -r pid; do
        if kill -0 "$pid" 2>/dev/null; then
            echo "Deteniendo proceso $pid..."
            kill "$pid"
        fi
    done < "$PIDS_FILE"
    rm -f "$PIDS_FILE"
else
    echo "No se encontró el archivo .services.pids."
    echo "Intentando detener procesos de spring-boot/mvnw de FerreFix..."
fi

# Buscamos procesos huérfanos de maven/spring-boot de forma general para limpiar por completo
echo "Buscando procesos huérfanos de maven/spring-boot..."
pkill -f "spring-boot:run"
pkill -f "mvnw"

echo ""
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;32m           TODOS LOS SERVICIOS HAN SIDO DETENIDOS\e[0m"
echo -e "\e[1;36m========================================================\e[0m"

read -p "Presiona Enter para continuar..."
