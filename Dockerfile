FROM maven:3.6.3-jdk-11-slim@sha256:68ce1cd457891f48d1e137c7d6a4493f60843e84c9e2634e3df1d3d5b381d36c AS build
RUN mkdir -p /project/sniffer
COPY . /project/sniffer
WORKDIR /project/sniffer
RUN mvn clean package -DskipTests

FROM adoptopenjdk/openjdk11:jre-11.0.9.1_1-alpine@sha256:b6ab039066382d39cfc843914ef1fc624aa60e2a16ede433509ccadd6d995b1f
RUN mkdir -p /app/sniffer
RUN addgroup --system javaoperator && adduser -S -s /bin/false -G javaoperator javaoperator
COPY --from=build /project/sniffer/target/*-SNAPSHOT.jar /app/sniffer/sniffer.jar
WORKDIR /app/sniffer
RUN chown -R javaoperator:javaoperator /app/sniffer
USER javaoperator
CMD "java" "-jar" "sniffer.jar"