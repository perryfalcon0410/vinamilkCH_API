FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./authorization-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/authorization-service/authorization-service-0.0.1-SNAPSHOT.jar
COPY ./machikatsu.key $PROJECT_HOME/authorization-service/machikatsu.key
COPY ./machikatsu.pub $PROJECT_HOME/authorization-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx1024m","-Xms512m","-Xmn64m","-jar","./authorization-service/authorization-service-0.0.1-SNAPSHOT.jar"]