FROM gradle:8.10.2-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/install/media-api-kotlin ./
EXPOSE 8080
CMD ["./bin/media-api-kotlin"]
