FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/photo-api.jar /photo-api/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/photo-api/app.jar"]
