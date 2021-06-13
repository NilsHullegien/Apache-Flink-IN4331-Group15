# Build the TYPES package
FROM maven:3.6.3-jdk-11 AS builder
COPY types/src /usr/src/types/src
COPY types/pom.xml /usr/src/types/pom.xml
RUN mvn -f /usr/src/types/pom.xml install -DskipTests

COPY flink/src /usr/src/app/src
COPY flink/pom.xml /usr/src/app
COPY web /usr/src/web
COPY /web/pom.xml /usr/src/web
RUN mvn -f /usr/src/app/pom.xml package -DskipTests
RUN mvn -f /usr/src/web/pom.xml package -DskipTests
#--mount=type=cache,target=/root/.m2
# ... and run the web server!
FROM openjdk:8
WORKDIR /
COPY --from=builder /usr/src/app/target/greeter-functions-app*jar-with-dependencies.jar greeter-functions-app.jar
COPY --from=builder /usr/src/web/target/kafka-sample-01*.jar webserver.jar

EXPOSE 1108 8080
CMD java -jar greeter-functions-app.jar & java -jar webserver.jar
#CMD java -jar webserver.jar
