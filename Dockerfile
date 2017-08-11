FROM openjdk:8-alpine

COPY build/libs/hypr.jar hypr.jar

CMD java -jar hypr.jar
