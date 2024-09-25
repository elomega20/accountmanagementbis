FROM adoptopenjdk/openjdk22:alpine-jre

ARG JAR_FILE=target/accountmanagement.jar

WORKDIR /opt/app

COPY ${JAR_FILE} accountmanagement.jar

COPY entrypoint.sh entrypoint.sh

RUN chmod 755 entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]