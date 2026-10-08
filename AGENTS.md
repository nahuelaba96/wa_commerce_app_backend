
---

# Agent Mission

Este agente está encargado de construir, mejorar y mantener el backend de **WhatsApp IA Commerce**, una API REST desarrollada con Java 21 y Spring Boot.

Puede implementar funcionalidades, corregir errores y crear o modificar pruebas, respetando los endpoints, modelos y patrones existentes en el proyecto.

Debe mantener la arquitectura actual por capas y procurar que las operaciones relacionadas con comercios respeten el `merchantId`.

---

# Project Context

- **Lenguaje:** Java 21
- **Framework backend:** Spring Boot 4.1.1
- **API:** REST con Spring Web MVC
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de datos:** PostgreSQL
- **Build y dependencias:** Maven
- **Validación:** Jakarta Bean Validation
- **Documentación de API:** Springdoc OpenAPI
- **Dominio:** comercios, usuarios, productos, categorías, variantes, clientes, conversaciones, mensajes y cuentas de WhatsApp

La configuración de Maven está en `pom.xml`. La conexión local a PostgreSQL está definida en `application.properties`.

---

# Architecture

El backend utiliza una **arquitectura por capas** dentro de una aplicación Spring Boot monolítica.

- **Controller:** expone los endpoints HTTP y recibe solicitudes.
- **DTO:** define los datos de entrada y salida de la API.
- **Service:** contiene la lógica de aplicación y coordina las operaciones.
- **Repository:** accede a los datos mediante Spring Data JPA.
- **Entity:** representa las entidades persistidas en PostgreSQL.
- **Mapper:** convierte entre entidades y DTOs.

Flujo habitual:

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL

Controller ← DTO ← Mapper ← Entity
```

## Reglas para los cambios

- Seguir la organización y los patrones existentes en `controller`, `dto`, `service`, `repository`, `entity` y `mapper`.
- Mantener la lógica de negocio en los servicios, no en los controladores.
- Usar DTOs para los contratos HTTP; evitar exponer directamente las entidades JPA.
- Usar repositorios para el acceso a datos y mappers para transformar entidades y respuestas.
- Validar los datos de entrada con las anotaciones de Jakarta Validation y `@Valid`, siguiendo los ejemplos existentes.
- Conservar la asociación de los datos al comercio correspondiente. Verificar la relación con `merchantId` cuando aplique.
- No introducir arquitectura hexagonal, microservicios u otra estructura distinta sin aprobación.
- No asumir que seguridad está activa: la dependencia de Spring Security está temporalmente deshabilitada en la configuración actual.
- No asumir que la integración con WhatsApp o un proveedor de IA está implementada; agregarla solo cuando la tarea lo requiera.
- Crear o actualizar pruebas para los cambios y ejecutar las pruebas Maven pertinentes.

---

# Autonomous Actions

El agente puede:

- Leer código.
- Buscar referencias.
- Leer documentación.
- Ejecutar tests.
- Ejecutar tests de integración.
- Compilar.
- Ejecutar la aplicación localmente.
- Ejecutar SELECT sobre bases locales/desechables autorizadas.
- Crear/modificar código dentro del scope aprobado.
- Crear/modificar tests dentro del scope aprobado.

---

# Actions Requiring Approval

Requieren aprobación humana:

- Cambiar contratos REST.
- Agregar dependencias.
- Cambiar arquitectura.
- Modificar schemas.
- Crear o ejecutar migrations.
- Modificar configuración sensible.
- Modificar autenticación/autorización.
- Deploy.
- Git push.

---

# Forbidden Actions

Está prohibido:

- Borrar bases de datos.
- Eliminar tests para conseguir que pasen.
- Desactivar controles de seguridad.
- Modificar código fuera del scope aprobado.
- Exponer secretos.
- Realizar cambios no relacionados con la feature.

---

# Validation

- Toda feature o bug fix debe tener tests que cubran el comportamiento
  nuevo o modificado, cuando corresponda.
- Deben ejecutarse los unit tests relevantes.
- Deben ejecutarse los integration tests relevantes.
- Deben ejecutarse las validaciones arquitectónicas y estáticas definidas
  por el proyecto.
- El agente nunca debe afirmar que una validación pasó sin haberla
  ejecutado realmente y obtenido un resultado exitoso.
- Si una validación no puede ejecutarse, debe informar explícitamente
  el motivo.
- El agente debe presentar un reporte de validación con los comandos
  ejecutados y sus resultados.
- El agente debe verificar que los cambios realizados permanecen dentro
  del scope aprobado.

---
# Escalation

El agente debe detener el trabajo y solicitar una decisión humana cuando:

- Existe una ambigüedad que puede afectar el comportamiento,
  arquitectura o scope aprobado.
- La implementación requiere una acción incluida en
  "Actions Requiring Approval".
- La implementación requiere credenciales, secretos o acceso
  a recursos no autorizados.
- La implementación requiere cambios fuera del scope aprobado.
- Aparece una decisión técnica relevante que no estaba contemplada
  en el plan aprobado.

Cuando escale, el agente debe:
1. Explicar el problema.
2. Explicar por qué no puede continuar autónomamente.
3. Presentar alternativas cuando existan, incluyendo ventajas y
   desventajas.
4. Recomendar una opción cuando tenga suficiente información.
5. Esperar una decisión humana antes de continuar.
   