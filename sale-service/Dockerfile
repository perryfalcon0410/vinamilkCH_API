#FROM openjdk:8-alpine
#
#RUN apk --no-cache add msttcorefonts-installer fontconfig && \
#    update-ms-fonts && \
#    fc-cache -f

FROM vnmchjdk:1.0

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./target/sale-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/sale-service/sale-service-0.0.1-SNAPSHOT.jar
COPY ./target/classes/machikatsu.pub $PROJECT_HOME/sale-service/machikatsu.pub

WORKDIR $PROJECT_HOME
CMD java $JAVA_OPTS -jar ./sale-service/sale-service-0.0.1-SNAPSHOT.jar
