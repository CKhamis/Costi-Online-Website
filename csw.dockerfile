FROM gradle:7.6.0-jdk17 as BUILDER
COPY . .
RUN gradle installBootDist

FROM amazoncorretto:17
COPY --from=BUILDER /home/gradle/build/install/csw9-boot .
EXPOSE 80
CMD ["./bin/csw9"]

