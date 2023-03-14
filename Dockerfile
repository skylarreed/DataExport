FROM maven:3.8.3-openjdk-17
LABEL maintainer="skylar.reed@smoothstack.com"
VOLUME /main-app
ADD target/dataexport-0.0.1-SNAPSHOT.jar data-export.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/data-processing.jar"]