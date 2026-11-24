#!/bin/bash

# This script setup gcloud configuration using application-default credentials

gcloud config configurations activate daily-expenses

gcloud auth application-default login --client-id-file=/Users/caiocaminha/Documents/STUDIES_PERSONAL_PROJECT/daily-expenses/src/main/resources/credentials.json \
--scopes=https://www.googleapis.com/auth/spreadsheets