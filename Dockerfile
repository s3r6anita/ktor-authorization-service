# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle clean build -i --stacktrace

# Stage 2: Build Application
FROM gradle:latest AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/app/
WORKDIR /usr/src/app
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:22 AS runtime
EXPOSE 8443
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-authorization-service-0.0.1.jar
COPY --from=build /home/gradle/src/build/*.jks /app/keystore.jks
ENTRYPOINT ["java","-jar","/app/ktor-authorization-service-0.0.1.jar"]