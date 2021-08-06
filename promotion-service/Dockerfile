FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/promotion-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/promotion-service/promotion-service-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/promotion-service/machikatsu.pub

WORKDIR $PROJECT_HOME
CMD java $JAVA_OPTS -jar ./promotion-service/promotion-service-0.0.1-SNAPSHOT.jar