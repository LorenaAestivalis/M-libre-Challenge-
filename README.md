# Documentación de Web Services

Este documento describe los web services disponibles para la gestión de productos, ventas y autenticación.

---

## 🚀 Productos

Web Service para operaciones relacionadas con productos.

### Endpoints

*   **`PUT /productos/cambiar-precio`**🔒 
    *   **Descripción:** Cambia el precio de un producto específico por su ID.
    *   **Método:** `PUT`
    *   **Ejemplo de Request Body:**
        ```json
        {
          "id": "string",
          "nuevoPrecio": 0.00
        }
        ```

*   **`POST /productos/crear-producto`**🔒 
    *   **Descripción:** Crea un nuevo producto.
    *   **Método:** `POST`
    *   **Ejemplo de Request Body:**
        ```json
        {
          "nombre": "string",
          "descripcion": "string",
          "precio": 0.00,
          "stock": 0
        }
        ```

*   **`GET /productos`**🔒 
    *   **Descripción:** Lista todos los productos disponibles.
    *   **Método:** `GET`
    *   **Parámetros:** Ninguno
    *   **Ejemplo de Response Body:**
        ```json
        [
          {
            "id": "string",
            "nombre": "string",
            "descripcion": "string",
            "precio": 0.00,
            "stock": 0
          }
        ]
        ```

*   **`GET /productos/consultar/{id}`**🔒 
    *   **Descripción:** Busca un producto por su ID.
    *   **Método:** `GET`
    *   **Parámetros de Ruta:**
        *   `id` (string): El ID único del producto a consultar.
    *   **Ejemplo de Response Body:**
        ```json
        {
          "id": "string",
          "nombre": "string",
          "descripcion": "string",
          "precio": 0.00,
          "stock": 0
        }
        ```

*   **`DELETE /productos/eliminar-producto/{id}`**🔒 
    *   **Descripción:** Elimina un producto por su ID.
    *   **Método:** `DELETE`
    *   **Parámetros de Ruta:**
        *   `id` (string): El ID único del producto a eliminar.

---

## 💰 Ventas

Web Service para operaciones relacionadas con ventas.

### Endpoints

*   **`POST /ventas/procesar`**🔒 
    *   **Descripción:** Procesa una nueva venta.
    *   **Método:** `POST`
    *   **Ejemplo de Request Body:**
        ```json
        {
          "productosVendidos": [
            {
              "productoId": "string",
              "cantidad": 0
            }
          ],
          "clienteId": "string"
        }
        ```

*   **`GET /ventas`**🔒 
    *   **Descripción:** Lista todas las ventas registradas.
    *   **Método:** `GET`
    *   **Parámetros:** Ninguno
    *   **Ejemplo de Response Body:**
        ```json
        [
          {
            "id": "string",
            "fecha": "YYYY-MM-DDTHH:MM:SSZ",
            "total": 0.00,
            "productos": [
              {
                "productoId": "string",
                "nombreProducto": "string",
                "cantidad": 0,
                "precioUnitario": 0.00
              }
            ],
            "clienteId": "string"
          }
        ]
        ```

*   **`GET /ventas/consultar/{id}`**🔒 
    *   **Descripción:** Obtiene los detalles de una venta por su ID.
    *   **Método:** `GET`
    *   **Parámetros de Ruta:**
        *   `id` (string): El ID único de la venta a consultar.
    *   **Ejemplo de Response Body:**
        ```json
        {
          "id": "string",
          "fecha": "YYYY-MM-DDTHH:MM:SSZ",
          "total": 0.00,
          "productos": [
            {
              "productoId": "string",
              "nombreProducto": "string",
              "cantidad": 0,
              "precioUnitario": 0.00
            }
          ],
          "clienteId": "string"
        }
        ```

---

## 🔒 Auth Controller

Web Service para autenticación.

### Endpoints

*   **`POST /auth/login`**
    *   **Descripción:** Realiza el inicio de sesión del usuario.
    *   **Método:** `POST`
    *   **Ejemplo de Request Body:**
        ```json
        {
          "username": "string",
          "password": "string"
        }
        ```
    *   **Ejemplo de Response Body (éxito):**
        ```json
        {
          "token": "your_auth_token_here",
          "expiresIn": 3600
        }
        ```
    *   **Ejemplo de Response Body (fallido):**
        ```json
        {
          "message": "Credenciales inválidas"
        }
        ```

---

## 🔐 Seguridad

Todos los endpoints marcados con el icono 🔒 requieren autenticación (token JWT en el encabezado `Authorization: Bearer <token>`).

---

## 📝 Notas Adicionales

*   Asegúrate de reemplazar los tipos de datos genéricos como "string" y los valores numéricos con los tipos y rangos de datos reales esperados.
*   Considera agregar información sobre códigos de estado HTTP (200 OK, 400 Bad Request, 401 Unauthorized, 404 Not Found, etc.).
*   Puedes incluir un ejemplo de cómo obtener un token de autenticación utilizando el endpoint `/auth/login` si lo consideras útil.
*   Si utilizas un framework como Swagger/OpenAPI, esta documentación puede generarse automáticamente.

Aquí tienes una vista previa de cómo se vería una sección con un ejemplo:

🚀 Productos

Web Service para operaciones relacionadas con productos.

PUT /productos/cambiar-precio

Descripción: Cambia el precio de un producto específico por su ID.

Método: PUT

Ejemplo de Request Body:

code
JSON
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
{
  "id": "PROD001",
  "nuevoPrecio": 25.99
}

Ejemplo de Response Body (éxito):

code
JSON
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
{
  "message": "Precio actualizado correctamente para el producto PROD001"
}

Códigos de Estado HTTP:

200 OK: Precio actualizado exitosamente.

400 Bad Request: Datos de entrada inválidos.

404 Not Found: Producto no encontrado.
