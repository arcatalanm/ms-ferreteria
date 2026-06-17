@echo off
title FerreFix - Limpiar Carpetas Target
echo ========================================================
echo             ELIMINANDO CARPETAS TARGET
echo ========================================================
echo.

setlocal enabledelayedexpansion

:: Lista de microservicios y componentes del proyecto
set SERVICES=ms_auth ms_direcciones ms_inventario ms_proveedores ms_usuarios ms_ventas ms_marcas ms_arriendo ms_compras ms_sugerencia ms_reportes api-gateway

for %%s in (%SERVICES%) do (
    if exist "%%s\target" (
        echo [OK] Eliminando %%s\target...
        rd /s /q "%%s\target"
    ) else (
        echo [INFO] %%s\target no existe o ya fue eliminada.
    )
)

echo.
echo ========================================================
echo             ¡LIMPIEZA COMPLETADA!
echo ========================================================
pause
