cd ms_direcciones
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

cd ../ms_inventario
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

cd ../ms_proveedores
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

cd ../ms_usuarios
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

cd ../ms_ventas
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

cd ../ms-gateway
:: Ejecutamos la siguiente accion
call ./mvnw clean package -DskipTest

