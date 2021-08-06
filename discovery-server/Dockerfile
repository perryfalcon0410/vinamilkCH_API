FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/discovery-server-0.0.1-SNAPSHOT.jar $PROJECT_HOME/discovery-server/discovery-server-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/discovery-server/machikatsu.pub

WORKDIR $PROJECT_HOME
CMD java $JAVA_OPTS -jar ./discovery-server/discovery-server-0.0.1-SNAPSHOT.jar