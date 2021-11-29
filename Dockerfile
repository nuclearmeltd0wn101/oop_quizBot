FROM openjdk:17-alpine3.14

ENV tgBotToken=""
ENV dbPath=/storage/QuizBot.sqlite

VOLUME /storage/
RUN mkdir /app/
WORKDIR /app/

COPY target/QuizBot-1.0-SNAPSHOT.jar /app/QuizBot.jar
COPY quiz_questions.txt /app/
COPY init_db /app/init_db

RUN apk add sqlite
CMD cd init_db; ash init_db_unix.sh $dbPath;cd ..; java -jar QuizBot.jar
