FROM openjdk:8-alpine

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./discovery-server-0.0.1-SNAPSHOT.jar $PROJECT_HOME/discovery-server/discovery-server-0.0.1-SNAPSHOT.jar

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./discovery-server/discovery-server-0.0.1-SNAPSHOT.jar"]