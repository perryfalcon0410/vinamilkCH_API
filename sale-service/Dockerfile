FROM openjdk:8-alpine

RUN apk --no-cache add msttcorefonts-installer fontconfig && \
    update-ms-fonts && \
    fc-cache -f

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY ./sale-service-0.0.1-SNAPSHOT.jar $PROJECT_HOME/sale-service/sale-service-0.0.1-SNAPSHOT.jar
COPY ./invoice-export.jrxml $PROJECT_HOME/sale-service/invoice-export.jrxml
COPY ./invoice-import.jrxml $PROJECT_HOME/sale-service/invoice-import.jrxml

WORKDIR $PROJECT_HOME

CMD ["java","-Xmx128m","-Xms16m","-Xmn64m","-jar","./sale-service/sale-service-0.0.1-SNAPSHOT.jar"]