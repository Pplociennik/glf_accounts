FROM openjdk:17.0.2-jdk
ARG JAR_FILE=docker/glf-accounts.jar
ARG JAVA_AGENT_FILE=docker/opentelemetry-javaagent-2.12.0.jar

# Debug (uncomment this to enable remote debugging on port 5005)
# ARG DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=y

ENV OPTS="$DEBUG_OPTS"

# Install curl using microdnf
RUN microdnf install -y curl

COPY ${JAR_FILE} app.jar
COPY ${JAVA_AGENT_FILE} /workspace/BOOT-INF/lib/opentelemetry-javaagent-2.12.0.jar
ENTRYPOINT java ${OPTS} -jar app.jar