FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./common-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/common-service/common-service-0.0.1-SNAPSHOT.jar

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./common-service/common-service-0.0.1-SNAPSHOT.jar"]