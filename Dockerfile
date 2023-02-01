FROM gradle:7.6.0-jdk17 as BUILDER
COPY . .
RUN gradle installBootDist
EXPOSE 8082
EXPOSE 80
RUN chmod +x start.sh
CMD ["./start.sh"]

# eventually get this to work for smaller image
#FROM amazoncorretto:11
#COPY --from=BUILDER /build/install/csw9-boot .
#EXPOSE 80
#CMD ["./csw9-boot/bin/csw9"]