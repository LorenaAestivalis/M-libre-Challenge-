API de Productos y Ventas
Este proyecto implementa una API RESTful para la gestión de productos y ventas, incluyendo la autenticación de usuarios. La API está diseñada para ser la capa de backend de una aplicación web o móvil, permitiendo la interacción con una base de datos de productos y transacciones de ventas.

Endpoints de la API
Productos
Conjunto de operaciones para la gestión del inventario de productos.

Método HTTP

Endpoint

Descripción

PUT

/productos/cambiar-precio

Actualiza el precio de un producto específico, identificado por su ID.

POST

/productos/crear-producto

Crea un nuevo producto en el sistema.

GET

/productos

Lista todos los productos disponibles.

GET

/productos/consultar/{id}

Busca y devuelve la información de un producto por su ID.

DELETE

/productos/eliminar-producto/{id}

Elimina un producto del sistema, identificado por su ID.

Ventas
Endpoints dedicados al procesamiento y consulta de transacciones de ventas.

Método HTTP

Endpoint

Descripción

POST

/ventas/procesar

Procesa una nueva transacción de venta.

GET

/ventas/consultar/{id}

Consulta y devuelve los detalles de una venta por su ID.

GET

/ventas

Lista todas las ventas registradas.

Autenticación
Gestión del acceso de usuarios a la API mediante JWT (JSON Web Tokens).

Método HTTP

Endpoint

Descripción

POST

/auth/login

Permite a un usuario autenticarse y obtener un token JWT para acceder a los endpoints protegidos.

Autenticación
La mayoría de los endpoints de esta API están protegidos y requieren un token JWT válido en el encabezado Authorization.

Ejemplo:

Authorization: Bearer <token_jwt_aqui>

Para obtener un token, debes usar el endpoint /auth/login con tus credenciales.

Cómo Usar
Clona el repositorio.

Configura tu entorno de desarrollo.

Inicia el servidor.

Utiliza herramientas como Postman, cURL, o la extensión de VS Code "REST Client" para interactuar con los endpoints.
