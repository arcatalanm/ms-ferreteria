@echo off
title Ferrefix - Detener Servicios Locales
echo ========================================================
echo             DETENIENDO SERVICIOS FERREFIX
echo ========================================================
echo.

:: Buscar y detener procesos escuchando en los puertos especificos
for %%p in (8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090 8092) do (
    for /f "tokens=5" %%i in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING') do (
        echo [OK] Deteniendo proceso %%i en puerto %%p...
        taskkill /f /pid %%i >nul 2>&1
    )
)

echo.
echo ========================================================
echo             ¡SERVICIOS DETENIDOS CON EXITO!
echo ========================================================
pause
