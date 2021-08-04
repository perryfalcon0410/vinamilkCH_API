#!/bin/bash

cd ../core-util
mvn clean
cd ..

cd core-processor
mvn clean
cd ..

cd discovery-server
mvn clean
cd ..

cd api-gateway
mvn clean
cd ..

cd authorization-service
mvn clean
cd ..

cd common-service
mvn clean
cd ..

cd customer-service
mvn clean
cd ..

cd promotion-service
mvn clean
cd ..

cd report-service
mvn clean
cd ..

cd sale-service
mvn clean
cd ..

