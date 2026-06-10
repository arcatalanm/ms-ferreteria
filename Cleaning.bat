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

echo Limpiando ms_marcas...
cd ../ms_marcas
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_arriendo...
cd ../ms_arriendo
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_compras...
cd ../ms_compras
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_sugerencia...
cd ../ms_sugerencia
call mvnw.cmd clean install -DskipTests

echo Limpiando ms_reportes...
cd ../ms_reportes
call mvnw.cmd clean install -DskipTests

echo Limpiando api-gateway...
cd ../api-gateway
call mvnw.cmd clean install -DskipTests

echo =========================================
echo ¡Todos los microservicios compilados!
echo =========================================
pause