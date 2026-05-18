# FerreFix - Sistema de Microservicios para Gestión de Ferreterías

FerreFix es una solución de arquitectura de microservicios diseñada para modernizar la administración y el almacenamiento de datos a gran escala en el rubro de la construcción. 

Al empaquetar el sistema en componentes independientes, garantizamos una alta disponibilidad y tolerancia a fallos: si un microservicio experimenta una caída, el resto de los procesos del negocio continúan operando sin interrupciones **(servidores independientes)**.

## Características Principales

* **Arquitectura de Microservicios:** Base de datos y lógica independientes por componente para evitar puntos únicos de fallo.
* **API Gateway Centralizada:** Puerta de enlace única en el puerto 8080 que administra y redirige todas las peticiones bajo un modelo API REST.
* **Infraestructura Dockerizada:** Despliegue automatizado mediante múltiples contenedores interconectados.
* **Validador de RUT Automatizado:** Implementación de la clase utilitaria `RutUtil` que evalúa RUN y dígito verificador (DV). Esto previene identidades falsas en registros de clientes, empleados y proveedores.
* **Trazabilidad con Logs Locales:** Cada microservicio genera su propio registro de eventos (logs) para auditoría, control de flujo y detección de errores (warnings). Los archivos se almacenan dentro del Workspace configurado en el `Dockerfile` de cada contenedor.

## 🛠️ Requisitos Previos

Antes de levantar el proyecto, asegúrate de tener instalado:

* **Sistema operativo Windows** (para la ejecución del script automatizado `.bat`).
* **Tener instalado DockerDesktop**. (para la dockerización)
* **Tener Paciencia y Tiempo**. (puede llevar un tiempo la instalacion de las imagenes `DB, JDK`)

## ⚙️ Instalación y Despliegue

El sistema cuenta con un script de automatización que compila el código Java y levanta el entorno de producción en un solo paso.

1. Abre una terminal de comandos (CMD o PowerShell) en la raíz del proyecto.
2. Ejecuta el archivo batch:
    Esto creará los ejecutables.jar de cada micro servicio (./mvnw clean package -DskipTests)
3. Ejecutar el docker-compose.yml (docker-compose up --build -d)
    Importante corroborar que no haya choque entre puertos u servicios externos influyendo .
4. Si todo compila, estaría listo para pruebas de ejecución via Peticiones HTTP.

## Comandos de uso
* ./mvnw spring-boot:run (ejecuta la aplicación)
* zip -r archivo.zip 'tag_directorio' (compresión del servicio a un .zip)
* ./mvnw clean package -DskipTests (crear ejecutables jar y saltarse etapa de ejecucion de la DB)

## Video Paso a Paso
* **https://www.youtube.com/watch?v=36Szd9ZM0WM**

## Comandos Docker
* docker compose up –build -d (construccion con los Dockerfile para los micro servicios)
* docker compose down -v (Bajar el Orquestador y los volumenes)
* docker exec -it 'nombre contenedor del ms' bash (conectarse al entorno aislado del micro servicio)
* docker ps (verficar servicios activos de docker)

