FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/common-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/common-service/common-service-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/common-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./common-service/common-service-0.0.1-SNAPSHOT.jar"]
