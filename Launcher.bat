@echo off
title FerreFix Launcher
echo ========================================================
echo                   INICIANDO FERREFIX
echo ========================================================
echo Para detener todo, presiona Ctrl+C.

:: 1. Microservicios (en segundo plano /b para no abrir ventanas nuevas)
echo [1/11] Levantando MS Direcciones...
cd ms_direcciones
start /b "Direcciones" cmd /c mvnw spring-boot:run
cd ..

echo [2/11] Levantando MS Inventario...
cd ms_inventario
start /b "Inventario" cmd /c mvnw spring-boot:run
cd ..

echo [3/11] Levantando MS Proveedores...
cd ms_proveedores
start /b "Proveedores" cmd /c mvnw spring-boot:run
cd ..

echo [4/11] Levantando MS Usuarios...
cd ms_usuarios
start /b "Usuarios" cmd /c mvnw spring-boot:run
cd ..

echo [5/11] Levantando MS Ventas...
cd ms_ventas
start /b "Ventas" cmd /c mvnw spring-boot:run
cd ..

echo [6/11] Levantando MS Marcas...
cd ms_marcas
start /b "Marcas" cmd /c mvnw spring-boot:run
cd ..

echo [7/11] Levantando MS Arriendo...
cd ms_arriendo
start /b "Arriendo" cmd /c mvnw spring-boot:run
cd ..

echo [8/11] Levantando MS Compras...
cd ms_compras
start /b "Compras" cmd /c mvnw spring-boot:run
cd ..

echo [9/11] Levantando MS Sugerencia...
cd ms_sugerencia
start /b "Sugerencia" cmd /c mvnw spring-boot:run
cd ..

echo [10/11] Levantando MS Reportes...
cd ms_reportes
start /b "Reportes" cmd /c mvnw spring-boot:run
cd ..

echo Esperando arranque de microservicios...
timeout /t 30 /nobreak

:: 2. API Gateway (Al final)
echo [11/11] Levantando API Gateway...
cd api-gateway
start /b "Gateway" cmd /c mvnw spring-boot:run
cd ..

echo.
echo ========================================================
echo   TODOS LOS SERVICIOS ESTAN ARRANCANDO EN SEGUNDO PLANO
echo   Tu API Gateway esta escuchando en http://localhost:8080
echo ========================================================
echo.
echo Presiona una tecla para ver los logs combinados aqui mismo...
pause > nul
echo Mostrando logs (puedes cerrar esta ventana cuando quieras)...
pause
