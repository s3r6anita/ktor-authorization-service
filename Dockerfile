FROM gradle:8.4-jdk17 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar

FROM openjdk:21
EXPOSE 80:80
COPY --from=build /app/build/libs/fat.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
