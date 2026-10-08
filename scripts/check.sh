#!/usr/bin/env bash

set -eu

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

run_validation() {
    local name="$1"
    shift

    printf '\n==> %s\n' "$name"
    printf 'Comando:'
    printf ' %q' "$@"
    printf '\n'

    if "$@"; then
        printf 'Resultado: OK\n'
    else
        local status=$?
        printf 'Resultado: FALLÓ (exit code %d)\n' "$status" >&2
        exit "$status"
    fi
}

run_validation "Build" ./mvnw -B -DskipTests package

run_validation \
    "Unit tests" \
    ./mvnw -B -Dspring.jpa.hibernate.ddl-auto=none '-Dtest=*Test' test

run_validation \
    "Integration tests (PostgreSQL efímero con Testcontainers)" \
    ./mvnw -B '-Dtest=*Tests' test

printf '\n==> Análisis estáticos\n'
printf 'Resultado: N/A (no hay análisis estáticos configurados en pom.xml)\n'

printf '\nTodas las validaciones configuradas pasaron.\n'
