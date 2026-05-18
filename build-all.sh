#!/bin/bash

echo "Iniciando limpieza y empaquetado de Microservicios - Ferrefix"

# Lista de carpetas actualizadas con los ms
servicios=(
    "ms-gateway"
    "ms_direcciones"
    "ms_usuarios"
    "ms_inventario"
    "ms_proveedores"
    "ms_ventas"
)

for ms in "${servicios[@]}"
do
    echo "------------------------------------------"
    echo "Procesando: $ms"
    echo "------------------------------------------"
    
    # Comprobar si el directorio existe antes de intentar entrar
    if [ -d "$ms" ]; then
        # Entramos al directorio de forma segura
        cd "$ms" || exit 1
        
        # Dar permisos de ejecución al wrapper por si acaso
        chmod +x mvnw
        
        # Ejecutar Maven y evaluar el resultado directamente
        if ./mvnw clean package -DskipTests; then
            echo "$ms compilado con éxito."
        else
            echo "Error al compilar $ms. Abortando todo el proceso."
            exit 1
        fi
        
        # Volver al directorio raíz
        cd ..
    else
        echo "No se encontró la carpeta: $ms. Saltando al siguiente..."
    fi
done

echo "------------------------------------------"
echo "¡Proceso finalizado con éxito! Todos los .jar están listos."