#!/bin/bash

cd ../core-util
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd core-processor
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd discovery-server
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd api-gateway
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd authorization-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd common-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd customer-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd promotion-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd report-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

cd sale-service
mvn clean
mvn install -Dmaven.test.skip=true
cd ..

