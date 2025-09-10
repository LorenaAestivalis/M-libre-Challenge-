# Manual de ejecucion Tienda API

Este documento explica cómo levantar el servidor de la API y cómo interactuar con los diferentes endpoints utilizando la interfaz de Swagger.

```markdown
# RUN.md
## Instrucciones para ejecutar el proyecto

Este documento explica cómo ejecutar el proyecto localmente.

### Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalados los siguientes programas:

*   Java 21 
*   Maven 3.5.5 
*   Git (para clonar el proyecto) 

### Clonar el repositorio

Clona el proyecto desde el repositorio remoto:

```bash
git clone [[https://github.com/usuario/nombre-proyecto.git
cd nombre-proyecto](https://github.com/LorenaAestivalis/M-libre-Challenge-.git)](https://github.com/LorenaAestivalis/M-libre-Challenge-.git)
```

### Construir el proyecto

Usando Maven:

```bash
mvn clean install
```

Esto descargará las dependencias y compilará el proyecto.

### Ejecutar el proyecto

#### Opción 1: Usando Maven

```bash
mvn spring-boot:run
```
``

El proyecto se ejecutará por defecto en el puerto 8443. Puedes cambiarlo en `application.properties` o `application.yml`.

### Acceder a la aplicación

*   API REST: `http://127.0.0.1:8443/api/` 
*   Documentación Swagger (si aplica): `https://127.0.0.1:8443/swagger-ui.html`
*   Tiene certificado SSL Autofirmado

### Ejecutar tests

```bash
mvn test
```

### Problemas comunes

*   **Dependencias faltantes:** Ejecuta `mvn clean install` nuevamente.
*   **Errores de Java:** Asegúrate de que la versión de Java sea la correcta.
```
