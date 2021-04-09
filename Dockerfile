FROM maven:3.6.3-jdk-14
WORKDIR /app
COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src /app/src
COPY tokens /app/tokens/
COPY credentials.json /app/src/main/resources
COPY application.yml /app
RUN mvn -B package

ENTRYPOINT exec java -jar /app/target/purchase-order-processor-1.0.0.jar -schedulingInterval 3600
