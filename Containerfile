FROM registry.access.redhat.com/ubi9-minimal

# ChatGPT had a hand in this. Was about 50/50

LABEL org.opencontainers.image.title="fetcharr" \
      org.opencontainers.image.description="Fetches upgrades for existing media" \
      org.opencontainers.image.source="local" \
      org.opencontainers.image.licenses="MIT"

ARG APP_USER=app
ARG APP_UID=1000
ARG APP_GID=1000
ARG JAR_FILE=App/target/fetcharr-1.1.5.jar

USER root

RUN microdnf install -y \
      java-21-openjdk-headless \
      shadow-utils \
      tzdata \
      glibc-langpack-en \
    && microdnf clean all \
    && rm -rf /var/cache/dnf

RUN groupadd -g "${APP_GID}" "${APP_USER}" \
    && useradd -u "${APP_UID}" -g "${APP_GID}" -d /app -s /sbin/nologin -M "${APP_USER}" \
    && mkdir -p /app /data /tmp \
    && chown -R "${APP_UID}:${APP_GID}" /app /data /tmp \
    && chmod 1777 /tmp

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom -Dfile.encoding=UTF-8"

WORKDIR /app

COPY entrypoint.sh /app/entrypoint.sh
COPY ${JAR_FILE} /app/fetcharr.jar

RUN chown "${APP_UID}:${APP_GID}" /app/entrypoint.sh /app/fetcharr.jar \
  && chmod 0755 /app/entrypoint.sh

USER ${APP_UID}:${APP_GID}

VOLUME ["/data"]

ENTRYPOINT ["/app/entrypoint.sh"]