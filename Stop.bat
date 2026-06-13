@echo off
title FerreFix Stopper
echo ========================================================
echo          DETENIENDO SERVICIOS DE FERREFIX
echo ========================================================

:: Lista de puertos a limpiar
for %%p in (8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090 8092) do (
    echo Buscando proceso en puerto %%p...
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING') do (
        echo - Deteniendo PID %%a...
        taskkill /f /pid %%a >nul 2>&1
    )
)

echo.
echo ========================================================
echo   Proceso finalizado. Si algun servicio seguia vivo,
echo   ha sido terminado.
echo ========================================================
pause
