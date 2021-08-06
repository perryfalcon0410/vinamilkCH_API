FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/api-gateway-0.0.1-SNAPSHOT.jar $PROJECT_HOME/api-gateway/api-gateway-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/api-gateway/machikatsu.pub

WORKDIR $PROJECT_HOME
CMD java $JAVA_OPTS -jar ./api-gateway/api-gateway-0.0.1-SNAPSHOT.jar