#!/bin/sh

# Requires maven to be on the classpath
# Skips test phase

mvn clean install -DskipTests=true
cd dhis-web
mvn clean install -DskipTests=true
cd ..

