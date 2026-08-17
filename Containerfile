FROM registry.access.redhat.com/ubi9-minimal

# ChatGPT had a hand in this. Was about 50/50

LABEL org.opencontainers.image.title="Fetcharr" \
      org.opencontainers.image.description="Hunts for missing or upgradable media in your *arr stack" \
      org.opencontainers.image.source="https://github.com/egg82/fetcharr" \
      org.opencontainers.image.documentation="https://github.com/egg82/fetcharr/blob/main/README.md" \
      org.opencontainers.image.authors="egg82" \
      org.opencontainers.image.licenses="MIT"

ARG JAR_FILE=App/target/fetcharr-2.1.1.jar

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom -Dfile.encoding=UTF-8"

USER root

RUN microdnf install -y \
      java-21-openjdk-headless \
      shadow-utils util-linux \
      tzdata \
      glibc-langpack-en \
    && microdnf clean all \
    && rm -rf /var/cache/dnf

RUN groupadd -g 1000 app \
    && useradd -u 1000 -g 1000 -d /app -s /sbin/nologin -M app \
    && mkdir -p /app /tmp \
    && chown -R 1000:1000 /app /tmp \
    && chmod 1777 /tmp

WORKDIR /app

COPY entrypoint.sh /app/entrypoint.sh
COPY start.sh /app/start.sh
COPY ${JAR_FILE} /app/fetcharr.jar

RUN chown 1000:1000 /app/start.sh /app/entrypoint.sh /app/fetcharr.jar \
  && chmod 0755 /app/start.sh /app/entrypoint.sh

USER 1000:1000

VOLUME ["/app/config", "/app/cache", "/app/logs", "/app/plugins"]

ENTRYPOINT ["/app/entrypoint.sh"]
