#!/bin/sh
set -eu

# Railway volumes may be mounted with root ownership. Fix permissions at startup,
# then run the Java process as the unprivileged spring user.
mkdir -p "${UPLOADS_DIR:-/app/uploads/student-photos}"
chown -R spring:spring /app/uploads
exec su-exec spring:spring java -XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -jar /app/app.jar
