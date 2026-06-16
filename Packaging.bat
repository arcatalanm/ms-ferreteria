@echo off
title FerreFix Packaging Tool
echo ========================================================
echo        EMPAQUETANDO PROYECTO FERREFIX (JAR)
echo ========================================================

:: Limpia cada 'Micro Servicio' y lo Empaqueta en un .jar
:: El ejecutable se guarda en la carpeta target del ms

echo Empaquetando ms_auth...
cd ms_auth
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_direcciones...
cd ../ms_direcciones
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_inventario...
cd ../ms_inventario
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_proveedores...
cd ../ms_proveedores
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_usuarios...
cd ../ms_usuarios
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_ventas...
cd ../ms_ventas
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_marcas...
cd ../ms_marcas
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_arriendo...
cd ../ms_arriendo
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_compras...
cd ../ms_compras
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_sugerencia...
cd ../ms_sugerencia
call mvnw clean package -DskipTests

echo.
echo Empaquetando ms_reportes...
cd ../ms_reportes
call mvnw clean package -DskipTests

echo.
echo Empaquetando api-gateway...
cd ../api-gateway
call mvnw clean package -DskipTests

echo.
echo ========================================================
echo   Todos los servicios han sido empaquetados en /target
echo ========================================================
pause