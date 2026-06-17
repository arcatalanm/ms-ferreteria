#!/bin/bash

# FerreFix - Limpiar Carpetas Target
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;36m             ELIMINANDO CARPETAS TARGET\e[0m"
echo -e "\e[1;36m========================================================\e[0m"
echo ""

# Lista de microservicios y componentes del proyecto
SERVICES=("ms_auth" "ms_direcciones" "ms_inventario" "ms_proveedores" "ms_usuarios" "ms_ventas" "ms_marcas" "ms_arriendo" "ms_compras" "ms_sugerencia" "ms_reportes" "api-gateway")

for service in "${SERVICES[@]}"; do
    if [ -d "$service/target" ]; then
        echo -e "\e[1;32m[OK]\e[0m Eliminando $service/target..."
        rm -rf "$service/target"
    else
        echo -e "\e[1;33m[INFO]\e[0m $service/target no existe o ya fue eliminada."
    fi
done

echo ""
echo -e "\e[1;36m========================================================\e[0m"
echo -e "\e[1;32m             ¡LIMPIEZA COMPLETADA!\e[0m"
echo -e "\e[1;36m========================================================\e[0m"

read -p "Presiona Enter para continuar..."
