FROM openjdk:8-jdk-slim as builder

# 根据 wrapper 下载 gradle
WORKDIR /home/gradle/src
COPY gradlew /home/gradle/src/gradlew
COPY gradle/wrapper /home/gradle/src/gradle/wrapper
RUN ./gradlew --version

# 编译项目
COPY . /home/gradle/src
RUN ./gradlew test :wx-api:build
RUN ls wx-api/build/libs/

FROM openjdk:8-jre-slim
ENV TZ Asia/Shanghai
EXPOSE 8080
COPY --from=builder /home/gradle/src/wx-api/build/libs/wx-api-*.jar /app/wx-api.jar
WORKDIR /app
CMD java -jar wx-api.jar
