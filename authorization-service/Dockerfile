FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/authorization-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/authorization-service/authorization-service-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.key $PROJECT_HOME/authorization-service/machikatsu.key
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/authorization-service/machikatsu.pub

WORKDIR $PROJECT_HOME
CMD java $JAVA_OPTS -jar ./authorization-service/authorization-service-0.0.1-SNAPSHOT.jar