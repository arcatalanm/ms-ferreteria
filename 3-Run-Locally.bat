@echo off
title FerreFix Launcher
echo ========================================================
echo                   INICIANDO FERREFIX LOCALLY
echo ========================================================
echo Para detener todo, presiona Ctrl+C o ejecuta 4-Stop-Locally.bat.

:: 1. Microservicios (en segundo plano /b para no abrir ventanas nuevas)
echo [1/12] Levantando MS Auth...
cd ms_auth
start /b "Auth" cmd /c mvnw spring-boot:run
cd ..

echo [2/12] Levantando MS Direcciones...
cd ms_direcciones
start /b "Direcciones" cmd /c mvnw spring-boot:run
cd ..

echo [3/12] Levantando MS Inventario...
cd ms_inventario
start /b "Inventario" cmd /c mvnw spring-boot:run
cd ..

echo [4/12] Levantando MS Proveedores...
cd ms_proveedores
start /b "Proveedores" cmd /c mvnw spring-boot:run
cd ..

echo [5/12] Levantando MS Usuarios...
cd ms_usuarios
start /b "Usuarios" cmd /c mvnw spring-boot:run
cd ..

echo [6/12] Levantando MS Ventas...
cd ms_ventas
start /b "Ventas" cmd /c mvnw spring-boot:run
cd ..

echo [7/12] Levantando MS Marcas...
cd ms_marcas
start /b "Marcas" cmd /c mvnw spring-boot:run
cd ..

echo [8/12] Levantando MS Arriendo...
cd ms_arriendo
start /b "Arriendo" cmd /c mvnw spring-boot:run
cd ..

echo [9/12] Levantando MS Compras...
cd ms_compras
start /b "Compras" cmd /c mvnw spring-boot:run
cd ..

echo [10/12] Levantando MS Sugerencia...
cd ms_sugerencia
start /b "Sugerencia" cmd /c mvnw spring-boot:run
cd ..

echo [11/12] Levantando MS Reportes...
cd ms_reportes
start /b "Reportes" cmd /c mvnw spring-boot:run
cd ..

echo Esperando arranque de microservicios...
timeout /t 30 /nobreak

:: 2. API Gateway (Al final)
echo [12/12] Levantando API Gateway...
cd api-gateway
start /b "Gateway" cmd /c mvnw spring-boot:run
cd ..

echo.
echo ========================================================
echo   TODOS LOS SERVICIOS ESTAN ARRANCANDO EN SEGUNDO PLANO
echo   Tu API Gateway esta escuchando en http://localhost:8080
echo ========================================================
echo.
echo Presiona una tecla para finalizar este script (los servicios seguiran corriendo)...
pause > nul
