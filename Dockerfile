ARG IMAGE_TAG_MAVEN=maven:3.9.1-eclipse-temurin-17
ARG IMAGE_TAG_JRE=eclipse-temurin:17-jre

FROM ${IMAGE_TAG_MAVEN} AS builder
WORKDIR /workplace
COPY pom.xml .
COPY src/main src/main
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


FROM ${IMAGE_TAG_JRE} AS layers
WORKDIR /tmp
ARG JAR_FILE=/workplace/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
WORKDIR /workplace
RUN java -Djarmode=layertools -jar /tmp/app.jar extract

FROM ${IMAGE_TAG_JRE}
WORKDIR /tmp
RUN adduser --disabled-password --gecos "" test-user
USER test-user
COPY --from=layers /workplace/dependencies/ ./
COPY --from=layers /workplace/snapshot-dependencies/ ./
COPY --from=layers /workplace/spring-boot-loader/ ./
COPY --from=layers /workplace/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
