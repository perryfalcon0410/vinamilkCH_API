#FROM openjdk:8-alpine
#
#RUN apk --no-cache add msttcorefonts-installer fontconfig && \
#    update-ms-fonts && \
#    fc-cache -f
FROM vnmchjdk:1.0

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/report-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/report-service/report-service-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/report-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./report-service/report-service-0.0.1-SNAPSHOT.jar"]
