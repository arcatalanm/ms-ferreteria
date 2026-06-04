@echo off
title FerreFix Launcher
echo ========================================================
echo               INICIANDO PROYECTO FERREFIX 
echo ========================================================
echo ASEGURATE DE TENER MYSQL ENCENDIDO ANTES DE CONTINUAR.
pause
echo.

:: 1. Iniciar Eureka Server
echo Levantando Eureka Server...
cd ms_eureka_server
start "Eureka Server" cmd /c mvnw spring-boot:run
cd ..
echo --- Esperando 15 segundos para que Eureka este 100%% operativo... ---
timeout /t 15 /nobreak

:: 2. Iniciar Microservicios independientes
echo.
echo Levantando MS Direcciones...
cd ms_direcciones
start "MS Direcciones" cmd /c mvnw spring-boot:run
cd ..

echo.
echo Levantando MS Inventario...
cd ms_inventario
start "MS Inventario" cmd /c mvnw spring-boot:run
cd ..

echo --- Esperando 45 segundos para que se validen los MS iniciales... ---
timeout /t 45 /nobreak

echo.
echo Levantando MS Proveedores...
cd ms_proveedores
start "MS Proveedores" cmd /c mvnw spring-boot:run
cd ..
echo --- Esperando 45 segundos para que MS Proveedores se registre... ---
timeout /t 45 /nobreak

echo.
echo Levantando MS Usuarios...
cd ms_usuarios
start "MS Usuarios" cmd /c mvnw spring-boot:run
cd ..
echo --- Esperando 45 segundos para que MS Usuarios se registre... ---
timeout /t 45 /nobreak

echo.
echo Levantando MS Ventas...
cd ms_ventas
start "MS Ventas" cmd /c mvnw spring-boot:run
cd ..
echo --- Esperando 45 segundos para que MS Ventas se registre... ---
timeout /t 45 /nobreak

echo.
echo Levantando API Gateway...
cd api-gateway
start "API Gateway" cmd /c mvnw spring-boot:run
cd ..
echo --- Esperando 15 segundos para la inicializacion del Gateway... ---
timeout /t 15 /nobreak

echo.
echo ========================================================
echo   Servicios Desplegados en ventanas individuales.
echo   Revisa http://localhost:8761 para ver todos en verde.
echo   Tu API Gateway esta escuchando en http://localhost:8080
echo ========================================================
pause