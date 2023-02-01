FROM amazoncorretto:17
COPY h2-1.4.190.jar h2-1.4.190.jar
EXPOSE 8082
VOLUME ["/root/data"]
CMD ["java", "-jar", "h2-1.4.190.jar","-tcpAllowOthers","-webAllowOthers"]

