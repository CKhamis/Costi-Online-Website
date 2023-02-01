FROM gradle:7.6.0-jdk17 as BUILDER
COPY . .
RUN gradle installBootDist
EXPOSE 80
CMD ["build/install/csw9-boot/bin/csw9"]

# eventually get this to work for smaller image
#FROM amazoncorretto:11
#COPY --from=BUILDER /build/install/csw9-boot .
#EXPOSE 80
#CMD ["./csw9-boot/bin/csw9"]

