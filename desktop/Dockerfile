FROM eclipse-temurin:21-jdk

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        libasound2t64 \
        libgl1 \
        libgtk-3-0 \
        libxext6 \
        libxi6 \
        libxrender1 \
        libxtst6 \
        libxxf86vm1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

ENV GRADLE_USER_HOME=/tmp/gradle-cache
ENV EXPENSE_API_BASE_URL=http://localhost:8080

CMD ["./gradlew", "run", "-Dexpense.api.baseUrl=http://localhost:8080"]
