FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./customer-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/customer-service/customer-service-0.0.1-SNAPSHOT.jar
COPY ./machikatsu.pub $PROJECT_HOME/authorization-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./customer-service/customer-service-0.0.1-SNAPSHOT.jar"]