FROM openjdk:17.0.2-jdk
ARG JAR_FILE=docker/glf-accounts.jar
ARG JAVA_AGENT_FILE=docker/opentelemetry-javaagent-2.12.0.jar

# Install curl using microdnf
RUN microdnf install -y curl

COPY ${JAR_FILE} app.jar
COPY ${JAVA_AGENT_FILE} /workspace/BOOT-INF/lib/opentelemetry-javaagent-2.12.0.jar
ENTRYPOINT ["java","-jar","/app.jar"]