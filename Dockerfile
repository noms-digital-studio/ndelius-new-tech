FROM openjdk:8-jre-slim

RUN apt-get update && \
    apt-get install -y curl jq && \
    rm -rf /var/lib/apt/lists/*

COPY target/scala-2.12/ndelius2-*.jar /root/app.jar

EXPOSE 9000

CMD ["java", "-jar", "/root/app.jar"]
