:: Limpia cada 'Micro Servicio' y lo Empaqueta en un .jar
:: El ejecutable se guarda en la carpeta target del ms

cd ms_direcciones
call ./mvnw clean package -DskipTest

cd ../ms_inventario
call ./mvnw clean package -DskipTest

cd ../ms_proveedores
call ./mvnw clean package -DskipTest

cd ../ms_usuarios
call ./mvnw clean package -DskipTest

cd ../ms_ventas
call ./mvnw clean package -DskipTest

cd ../ms-gateway
call ./mvnw clean package -DskipTest

