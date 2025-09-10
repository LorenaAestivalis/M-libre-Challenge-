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
         "username": "admin",
         "password": "M3rc4d0*L1br3%2025"
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
Es caseSensitive en en el usuario
{
  "username": "user",
  "password": "M3rc4d0*L1br3%2025"
}
{
  "username": "admin",
  "password": "M3rc4d0*L1br3%2025"
}

---

## 📝 Notas Adicionales

*   Asegúrate de ingresar el usuario con minusculas porque el elemento es case sensitive

