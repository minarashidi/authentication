#!/usr/bin/env bash

DOCKER_CONTAINER=postgres
PG_USER=postgres
DATABASE=authentication

echo "Removing ${DATABASE}"
docker exec -i ${DOCKER_CONTAINER} dropdb ${DATABASE} --username=${PG_USER}

echo "Creating ${DATABASE}"
docker exec -i ${DOCKER_CONTAINER} createdb ${DATABASE} --username=${PG_USER}
