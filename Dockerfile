FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:21-jdk-slim

WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test

EXPOSE 8089

CMD ["java", "-jar", "build/libs/optical-manage-0.0.1-SNAPSHOT.jar"]