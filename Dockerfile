FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/optical-manage-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8089

CMD ["java", "-jar", "app.jar"]