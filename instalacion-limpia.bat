@echo off
:: Limpia y Compila cada proyecto saltandose la etapa de pruebas

echo Limpiando ms_direcciones...
cd ms_direcciones
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_inventario...
cd ../ms_inventario
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_proveedores...
cd ../ms_proveedores
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_usuarios...
cd ../ms_usuarios 
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_ventas...
cd ../ms_ventas 
call mvnw.cmd clean install -DskipTests

echo Limpiando api-gateway...
cd ../api-gateway
call mvnw.cmd clean install -DskipTests

echo =========================================
echo ¡Todos los microservicios compilados!
echo =========================================
pause