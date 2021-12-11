FROM gradle:7.3-jdk17 AS build
ENV APP_HOME=/btc_eth_converter/
WORKDIR $APP_HOME
COPY --chown=gradle:gradle . .
RUN gradle build


FROM openjdk:17-alpine
ENV ARTIFACT_NAME=btc-eth-converter-1.0-SNAPSHOT.jar
ENV APP_HOME=/btc_eth_converter/
ENV PORT=8000
WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$ARTIFACT_NAME .

EXPOSE $PORT
ENTRYPOINT exec java -jar ${ARTIFACT_NAME} $PORT
