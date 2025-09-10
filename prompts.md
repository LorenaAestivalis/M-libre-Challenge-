


---

### `prompts.md` - Prompts Utilizados para el Proyecto Java "Tienda - Web Services "

Este documento contiene los prompts que se utilizaron con herramientas de IA para asistir en el desarrollo del proyecto "Tienda - Web Services", enfocándose en la aplicación de buenas prácticas de programación Java, diseño y arquitectura.

---

#### **1. Planificación y Diseño de Arquitectura**

**Objetivo:** Establecer una estructura sólida, escalable y con mantenibilidad  para la aplicación.

*   **Prompt 1.1: Diseño de repositorio**
    ```
    "Requiero  trabajar  los registros de datos en un archivo con extension '.csv' para la lectura y escritura de registro de mi clase producto sin usar una base de datos. Ayudame hacerlo implementando buenas practicas de persistencia , segridad y arquitectura de microservicios."
    ```
*   **Prompt 1.2: HandlerExeption**
    ```
    "El control de errores ayudame a consolidarlo en una clase global para  mejor gestion. "
*   **Prompt 1.3: Hardening**
    ```
    "Ayudame a hardenizar mi servicio https con el fin de manejar CSP."


#### **2. Desarrollo de Clases y Lógica de Negocio**

**Objetivo:** Implementar las entidades y la lógica central de la aplicación.

*   **Prompt 2.1: `Producto`**
    ```
    "Ayudame a validar mis clases con el fin de que aplique buenas practicas.
    IA   usar  @Data @NoArgsConstructor @AllArgsConstructor para que el codigo se mas lipio y sofisticado.  "
    ```

---



#### **5. Documentación y Explicación**

**Objetivo:** La exposición del Swagger

*   **Prompt 5.1: Documentacion**
    ```
    "Cuando Integre la autenticacion con mis serviciosse presento lo siguiente:
    Genera error la compilacion por conflicto de versiones con dependency.
    Adicional no me cargaba ya Swagger en el navegador  y se debia porque me falta integrar OpenApiConfig "
    ```

---
