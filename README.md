# Backend — WhatsApp IA Commerce

API REST para administrar comercios y sus datos de catálogo y atención. Está implementada como una aplicación monolítica con Java 21 y Spring Boot, y persiste la información en PostgreSQL.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA e Hibernate
- PostgreSQL
- Jakarta Bean Validation
- Springdoc OpenAPI / Swagger UI
- Maven (incluye Maven Wrapper)

## Estructura

El código principal se encuentra en `src/main/java/com/example/backend`:

| Paquete | Responsabilidad |
| --- | --- |
| `controller` | Define los endpoints HTTP y los códigos de respuesta. |
| `dto` | Define los contratos de entrada y salida de la API. |
| `service` | Implementa la lógica de aplicación. |
| `repository` | Accede a los datos mediante Spring Data JPA. |
| `entity` | Modela las entidades persistidas. |
| `mapper` | Convierte entre entidades y DTOs. |
| `config` | Configuración transversal, incluido CORS. |

El flujo habitual es `Controller → Service → Repository → PostgreSQL`; los DTOs y mappers mantienen los contratos HTTP separados de las entidades JPA.

### Dominios

La API cubre comercios y membresías de usuarios, usuarios, productos, categorías, imágenes y variantes, clientes, conversaciones, mensajes y cuentas de WhatsApp.

## API REST

Todas las rutas de negocio usan el prefijo `/api`. Los endpoints de listado devuelven recursos asociados al `merchantId`, salvo las consultas específicas de comercios y membresías.

| Recurso | Rutas |
| --- | --- |
| Comercios | `GET /api/merchants/user/{userID}`, `GET /api/merchants/{merchantId}`, `POST /api/merchants` |
| Membresías | `GET /api/merchant-users/merchant/{merchantId}`, `POST /api/merchant-users` |
| Usuarios | `GET /api/users?merchantId={id}`, `POST /api/users` |
| Categorías | `GET /api/categories?merchantId={id}`, `POST /api/categories` |
| Productos | `GET /api/products?merchantId={id}`, `POST /api/products`, `PUT /api/products?merchantId={id}&id={productId}`, `DELETE /api/products?merchantId={id}&id={productId}` |
| Imágenes de producto | `GET /api/product-images?merchantId={id}`, `POST /api/product-images` |
| Variantes de producto | `GET /api/product-variants?merchantId={id}`, `POST /api/product-variants`, `PUT /api/product-variants?merchantId={id}&id={variantId}` |
| Clientes | `GET /api/customers?merchantId={id}`, `POST /api/customers` |
| Conversaciones | `GET /api/conversations?merchantId={id}`, `POST /api/conversations` |
| Mensajes | `GET /api/messages?merchantId={id}`, `POST /api/messages` |
| Cuentas de WhatsApp | `GET /api/whatsapp-accounts?merchantId={id}`, `POST /api/whatsapp-accounts` |

Las solicitudes `POST` reciben JSON y sus cuerpos se validan con Jakarta Validation (`@Valid`). En general, las creaciones responden `201 Created`, los listados `200 OK`, el borrado de producto `204 No Content` y las solicitudes inválidas `400 Bad Request`.

Las solicitudes `PUT` también reciben JSON validado y responden `200 OK`. Para productos, el cuerpo requiere `name` y `categoryId` (la categoría debe pertenecer al comercio indicado); `description` y `brand` son opcionales. Si se omite `brand` al editar, el valor se guarda como `null`. Para variantes, el cuerpo incluye `sku`, `size`, `color`, `price` y `stock`. Ambos endpoints verifican que el recurso pertenezca al `merchantId`; las variantes se editan individualmente y no cambian de producto.

### Documentación y monitoreo

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Especificación OpenAPI: `http://localhost:8080/v3/api-docs`
- Actuator: `/actuator/health`, `/actuator/info` y `/actuator/metrics`

## Ejecución local

### Requisitos

- JDK 21
- PostgreSQL disponible localmente
- Docker en ejecución para las pruebas de integración

### Preparar la base de datos

La configuración actual espera una base PostgreSQL llamada `whatsapp_ia_commerce` en `localhost:5432`. Créala si aún no existe:

```sql
CREATE DATABASE whatsapp_ia_commerce;
```

Antes de iniciar, configura el usuario y la contraseña de PostgreSQL en `src/main/resources/application.properties`; ambos valores están vacíos en la configuración versionada. Hibernate usa `spring.jpa.hibernate.ddl-auto=update` para actualizar el esquema al iniciar, por lo que esta configuración está pensada para desarrollo local, no como estrategia de migraciones de producción.

La configuración local de entorno usada por el perfil de ejecución de VS Code se guarda en `backend/.env`, que está excluido de Git. No guardes secretos en archivos versionados.

### Iniciar

Desde el directorio `backend/`:

```bash
./mvnw spring-boot:run
```

La API queda disponible por defecto en `http://localhost:8080`.

## Pruebas y build

Ejecuta todas las pruebas:

```bash
./mvnw test
```

Empaqueta la aplicación:

```bash
./mvnw -DskipTests package
```

El script `scripts/check.sh` ejecuta el build, las pruebas unitarias y las de integración definidas por el proyecto. Se puede lanzar desde `backend/` con:

```bash
./scripts/check.sh
```

No hay análisis estático configurado actualmente en `pom.xml`; el script informa esa validación como no disponible.

Las pruebas de integración usan Testcontainers para iniciar un PostgreSQL efímero y aislado. Hibernate crea y elimina el esquema únicamente dentro de ese contenedor; las pruebas no se conectan a la base local configurada para ejecutar la aplicación. Antes de ejecutar `./scripts/check.sh`, asegúrate de que Docker esté iniciado.

## Configuración y alcance actual

- CORS permite `http://localhost:4200` por defecto. Configura `BACKEND_CORS_ALLOWED_ORIGINS` como lista separada por comas de orígenes explícitos para otros entornos; no se admite `*`.
- `AUTH_JWKS_URL`, `AUTH_ISSUER` y `AUTH_AUDIENCE` configuran la validación local de access tokens. Deben coincidir con el servicio de autenticación.
- `INTERNAL_SERVICE_TOKEN` debe ser el mismo secreto gestionado fuera del repositorio en backend y autenticación; habilita el endpoint privado de onboarding.
- Las rutas `/api/**` requieren un JWT válido y restringen los datos al `merchant_id` del token. No expongas el endpoint interno de onboarding fuera de la red privada entre servicios.
- La presencia de endpoints para cuentas de WhatsApp no implica que exista una integración con WhatsApp o con un proveedor de IA.
- Los detalles de conexión y Actuator se configuran en `src/main/resources/application.properties`.