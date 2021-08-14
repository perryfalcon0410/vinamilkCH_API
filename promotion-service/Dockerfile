FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./promotion-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/promotion-service/promotion-service-0.0.1-SNAPSHOT.jar
COPY ./machikatsu.pub $PROJECT_HOME/promotion-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx1024m","-Xms512m","-Xmn64m","-jar","./promotion-service/promotion-service-0.0.1-SNAPSHOT.jar"]