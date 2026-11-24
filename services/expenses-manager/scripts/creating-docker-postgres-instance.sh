#!/bin/bash

# This script setup an instance of postgres database on port 5432

docker run --name daily-expenses-pg -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres