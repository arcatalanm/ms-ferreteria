#!/bin/bash

# FerreFix Packaging Tool
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;36m        EMPAQUETANDO PROYECTO FERREFIX (JAR)\e[0m"
echo -e "\e[1;36m========================================================\e[0m"
echo ""

# Guardamos el directorio base del script
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

SERVICES=("ms_auth" "ms_direcciones" "ms_inventario" "ms_proveedores" "ms_usuarios" "ms_ventas" "ms_marcas" "ms_arriendo" "ms_compras" "ms_sugerencia" "ms_reportes" "api-gateway")

for service in "${SERVICES[@]}"; do
    echo -e "\e[1;34mEmpaquetando $service...\e[0m"
    if [ -d "$BASE_DIR/$service" ]; then
        cd "$BASE_DIR/$service" || continue
        # Asegurarse de que mvnw sea ejecutable
        if [ -f "mvnw" ]; then
            chmod +x mvnw
            ./mvnw clean package -DskipTests
        else
            echo -e "\e[1;31m[ERROR]\e[0m No se encontró mvnw en $service"
        fi
    else
        echo -e "\e[1;31m[ERROR]\e[0m El directorio $service no existe"
    fi
    echo ""
done

echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;32m  Todos los servicios han sido empaquetados en /target\e[0m"
echo -e "\e[1;36m========================================================\e[0m"

read -p "Presiona Enter para continuar..."
