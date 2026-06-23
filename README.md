# Ferrefix - Plataforma de Microservicios para Ferreterías

Ferrefix es un ecosistema distribuido y escalable diseñado bajo una arquitectura de microservicios para la gestión integral de ferreterías. El sistema implementa patrones modernos como **API Gateway**, **Database-per-Service** (aislamiento de datos), **seguridad integrada** y **carga dinámica de datos** para entornos de desarrollo.

<p align="center">
  <img src="./logoferrefix.png" alt="Ferrefix Logo" width="200px">
</p>

---

## 📦 Catálogo de Servicios

| Servicio | Puerto | Descripción | Base de Datos |
| :--- | :---: | :--- | :--- |
| **api-gateway** | `8080` | Punto de entrada único, ruteo dinámico y balanceo. | *Ninguna* |
| **ms_auth** | `8092` | Autenticación de usuarios y emisión de tokens. | `ferrefixauth` |
| **ms_usuarios** | `8082` | Gestión de perfiles, clientes y administradores. | `ferrefixusr` |
| **ms_inventario** | `8081` | Control de stock de productos y herramientas. | `ferrefixinv` |
| **ms_ventas** | `8083` | Procesamiento de ventas, boletas y facturas. | `ferrefixventas` |
| **ms_proveedores** | `8084` | Gestión de proveedores e insumos. | `ferrefixprov` |
| **ms_direcciones** | `8085` | Ubicaciones físicas y sucursales. | `ferrefixadress` |
| **ms_marcas** | `8086` | Marcas y fabricantes de productos. | `ferrefixmarcas` |
| **ms_arriendo** | `8087` | Alquiler de maquinaria y herramientas pesadas. | `ferrefixarriendo` |
| **ms_compras** | `8088` | Órdenes de compra y reposición de stock. | `ferrefixcompras` |
| **ms_sugerencia** | `8089` | Buzón de feedback, reclamos y sugerencias. | `ferrefixsug` |
| **ms_reportes** | `8090` | Estadísticas del negocio y reportes financieros. | *Ninguna* |

---

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java 21+, Spring Boot 3.x && 4.x, Spring Cloud Gateway, JPA / Hibernate.
* **Base de Datos:** MySQL 8.0.
* **Pruebas y Datos:** JUnit 5, DataFaker (generación programática de datos de prueba).
* **Documentación de APIs:** OpenAPI 3 / Swagger (Springdoc OpenAPI UI).
* **Contenedores y Orquestación:** Docker y Docker Compose.

---

## Requisitos Previos

Asegúrate de tener instalado en tu máquina local:
* **Java JDK 21**.
* **Maven** (o usa el wrapper `./mvnw` incluido en cada directorio).
* **Docker Desktop o Service** (con soporte para Docker Compose).
* Sistema Operativo Windows (para la ejecución directa de los scripts `.bat`).

---

## Cómo Ejecutar el Proyecto

Tienes dos alternativas para levantar el entorno:

### Opción A: Ejecución Local en Desarrollo 

1. **Limpiar todo:** Ejecuta `1-Clean-All.bat` para eliminar compilaciones previas.
2. **Levantar localmente:** Ejecuta `3-Run-Locally.bat`. Levantará de forma ordenada los 11 microservicios en segundo plano, esperará 30 segundos a que estén listos, y finalmente iniciará el API Gateway.
3. **Detener servicios:** Si deseas finalizar la ejecución, abre y corre `4-Stop-Locally.bat` (o finaliza los procesos Java de tu sistema).

### Opción B: Orquestación Completa con Docker
Si prefieres aislar todo el entorno, incluido el motor de bases de datos:

1. Ejecuta `2-Build-JARs.bat` para empaquetar todos los servicios.
```bash
# Construir las imágenes y levantar los contenedores en red interna
docker compose up --build -d
```
*Esto iniciará contenedores independientes para cada base de datos MySQL y cada microservicio de manera automática.*

---