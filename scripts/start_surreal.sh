#!/bin/bash

echo "Starting SurrealDB container..."
docker run --rm --pull always -p 8000:8000 surrealdb/surrealdb:latest start
echo "SurrealDB container started"
