FROM openjdk:8-alpine

RUN apk --no-cache add msttcorefonts-installer fontconfig && \
    update-ms-fonts && \
    fc-cache -f

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./customer-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/customer-service/customer-service-0.0.1-SNAPSHOT.jar
COPY ./machikatsu.pub $PROJECT_HOME/customer-service/machikatsu.pub

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx1024m","-Xms512m","-Xmn64m","-jar","./customer-service/customer-service-0.0.1-SNAPSHOT.jar"]