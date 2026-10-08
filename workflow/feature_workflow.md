# Feature Development Workflow

Este documento define el workflow obligatorio que el agente debe seguir
para implementar features, corregir bugs o modificar tests.

El workflow es:

DISCOVER → PLAN → HUMAN APPROVAL → BASELINE → IMPLEMENT → VALIDATE → HUMAN REVIEW

Si durante IMPLEMENT o VALIDATE aparece una decisión fuera del scope aprobado,
el agente debe detenerse y ejecutar el proceso de ESCALATION.

---

# 1. DISCOVER

Objetivo: comprender el problema y el estado actual del proyecto antes de
proponer una solución.

Durante esta fase el agente puede leer e inspeccionar el proyecto, pero
NO puede modificar archivos.

Debe:

- Leer `AGENTS.md`.
- Leer la documentación relevante.
- Inspeccionar la estructura del proyecto.
- Revisar la arquitectura existente.
- Revisar los módulos/componentes relacionados con la tarea.
- Buscar implementaciones existentes relacionadas con el requerimiento.
- Buscar tests existentes relacionados con el comportamiento.
- Revisar contratos existentes.
- Revisar `pom.xml` y las dependencias relevantes.
- Identificar restricciones técnicas.
- Identificar posibles impactos sobre otros componentes.
- Identificar decisiones que requieran aprobación humana.

El agente debe evitar implementar una solución antes de haber completado
esta fase.

Al finalizar debe tener suficiente información para elaborar un PLAN.

---

# 2. PLAN

El agente debe proponer un plan antes de modificar código.

El PLAN debe contener como mínimo:

## Feature / Problem

Descripción breve del problema o funcionalidad solicitada.

## Proposed Solution

Descripción de la solución propuesta.

## Decisions

Decisiones técnicas relevantes y justificación de cada una.

## Affected Components

Componentes, módulos y archivos que probablemente serán modificados.

## Proposed Changes

Cambios concretos que se realizarán.

## Approved Scope

Lista explícita de lo que el agente está autorizado a modificar.

## Out of Scope

Cambios que NO forman parte de esta tarea.

## Tests

Tests que deberán crearse o modificarse.

## Validation

Validaciones que deberán ejecutarse después de la implementación.

## Risks and Assumptions

Riesgos, supuestos o decisiones que podrían requerir intervención humana.

---

# 3. HUMAN APPROVAL

Después de presentar el PLAN, el agente debe detenerse y esperar
una aprobación humana explícita.

La solicitud original de la feature NO constituye automáticamente
la aprobación del PLAN.

El agente no debe comenzar la implementación hasta recibir una
aprobación explícita.

Ejemplos de aprobación válida:

- "Aprobado"
- "Aprobado el plan"
- "Procedé con ese plan"

Si el humano modifica el PLAN:

1. El agente debe actualizar el PLAN.
2. Debe volver a presentarlo.
3. Debe esperar una nueva aprobación explícita.

Si el humano rechaza el PLAN:

1. El agente debe detenerse.
2. Debe solicitar nuevas instrucciones o proponer una alternativa
   si corresponde.

---

# 4. BASELINE

Después de recibir aprobación y antes de modificar código, el agente
debe ejecutar las validaciones actuales del proyecto.

El objetivo es conocer el estado del proyecto antes de implementar
los cambios.

Debe utilizar el mecanismo de validación definido por el proyecto,
preferentemente:

    ./scripts/check.sh

El agente debe registrar:

- comandos ejecutados;
- resultado de cada validación;
- exit code;
- tests fallidos;
- errores existentes.

Si existen fallos antes de comenzar la implementación, deben ser
identificados como PRE-EXISTING FAILURES.

El agente no debe asumir que los fallos fueron causados por su trabajo
si ya existían en el baseline.

Si el baseline no puede ejecutarse, el agente debe informar el motivo
antes de continuar.

---

# 5. IMPLEMENT

Una vez aprobado el PLAN y registrado el BASELINE, el agente puede
implementar la solución.

Debe:

- respetar `AGENTS.md`;
- respetar la arquitectura definida;
- modificar únicamente archivos dentro del Approved Scope;
- implementar los cambios definidos en el PLAN;
- crear o modificar los tests necesarios;
- reutilizar componentes existentes cuando corresponda;
- evitar cambios no relacionados con la tarea.

El agente puede realizar cambios internos necesarios para implementar
correctamente la solución siempre que estén dentro del Approved Scope.

---

# 6. SCOPE CONTROL

Durante IMPLEMENT, el agente debe verificar continuamente que los
cambios realizados permanezcan dentro del Approved Scope.

Si descubre que necesita realizar un cambio que:

- no estaba incluido en el PLAN;
- modifica un contrato;
- requiere una nueva dependencia;
- modifica la arquitectura;
- modifica el schema de la base de datos;
- requiere una migration;
- modifica configuración sensible;
- requiere deploy;
- requiere `git push`;
- necesita credenciales;
- introduce una decisión técnica importante no contemplada;

debe detenerse.

No debe realizar automáticamente el cambio.

Debe ejecutar el proceso de ESCALATION.

---

# 7. ESCALATION

Cuando aparece una decisión fuera de la autoridad del agente:

1. Detener la implementación relacionada con esa decisión.
2. Explicar el problema.
3. Explicar por qué la decisión no puede tomarse autónomamente.
4. Presentar las alternativas disponibles.
5. Para cada alternativa indicar:
   - ventajas;
   - desventajas;
   - impacto;
   - riesgos.
6. Recomendar una alternativa cuando sea posible.
7. Solicitar una decisión humana explícita.

El agente no debe continuar utilizando una alternativa no aprobada.

Si la decisión modifica el Approved Scope o el PLAN:

1. actualizar el PLAN;
2. actualizar el Approved Scope;
3. presentar el nuevo PLAN;
4. esperar aprobación humana;
5. continuar únicamente después de recibirla.

Una aprobación para resolver una escalación no implica automáticamente
la aprobación de todos los cambios relacionados.

---

# 8. VALIDATE

Después de finalizar la implementación, el agente debe ejecutar
las validaciones correspondientes.

Preferentemente debe utilizar:

    ./scripts/check.sh

Como mínimo, cuando estén configuradas en el proyecto, deben validarse:

- compilación;
- unit tests;
- integration tests;
- architecture tests;
- static analysis;
- formato;
- demás validaciones definidas por el proyecto.

El agente debe verificar además que los cambios realizados permanezcan
dentro del Approved Scope.

Debe revisar:

- archivos modificados;
- tests modificados;
- dependencias modificadas;
- configuración modificada;
- cambios no relacionados.

---

# 9. VALIDATION REPORT

El agente debe generar un reporte de validación.

El reporte debe indicar exactamente qué comandos fueron ejecutados
y cuál fue su resultado.

Ejemplo:

    VALIDATION REPORT

    Build:
    PASS - ./mvnw compile

    Unit tests:
    PASS - 42 tests, 0 failures

    Integration tests:
    PASS - 12 tests, 0 failures

    Architecture tests:
    PASS - 8 tests, 0 violations

    Static analysis:
    PASS

    Scope verification:
    PASS

    Final result:
    PASS

El agente nunca debe afirmar que una validación pasó si no ejecutó
el comando correspondiente y obtuvo un resultado exitoso.

Si una validación no pudo ejecutarse debe indicar:

- qué validación no pudo ejecutarse;
- por qué;
- qué comando intentó ejecutar;
- qué resultado obtuvo.

No debe inventar resultados.

---

# 10. HUMAN REVIEW

Después de completar VALIDATE, el agente debe presentar el resultado
para revisión humana.

Debe informar como mínimo:

- resumen de lo implementado;
- archivos modificados;
- tests creados o modificados;
- validaciones ejecutadas;
- resultados;
- riesgos conocidos;
- limitaciones;
- cambios pendientes;
- cualquier desviación respecto del PLAN original.

El agente debe esperar la revisión humana.

La HUMAN REVIEW no constituye automáticamente autorización para
realizar nuevos cambios.

Si durante la revisión humana se solicitan cambios adicionales,
estos deben volver al workflow correspondiente.

Si los cambios son nuevos o modifican el scope:

    PLAN → HUMAN APPROVAL → BASELINE → IMPLEMENT → VALIDATE → HUMAN REVIEW

---

# 11. REGLAS GENERALES

- No modificar código durante DISCOVER.
- No implementar antes de recibir aprobación explícita del PLAN.
- No realizar cambios fuera del Approved Scope.
- No saltar BASELINE cuando pueda ejecutarse.
- No continuar ante una decisión que requiera aprobación.
- No ocultar fallos de validación.
- No eliminar tests para conseguir una validación exitosa.
- No afirmar que una validación pasó sin ejecutarla.
- No inventar resultados.
- No utilizar credenciales de producción.
- No modificar recursos de producción.
- No convertir una aprobación puntual en autorización general.
- Toda ampliación significativa del scope requiere nueva aprobación.

El objetivo del workflow es permitir autonomía dentro de límites
claramente definidos, manteniendo las decisiones importantes bajo
control humano.