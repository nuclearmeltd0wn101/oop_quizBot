FROM openjdk:17-alpine3.14

# paste your token here
ENV tgBotToken=""
ENV dbPath=/storage/QuizBot.sqlite

VOLUME /storage/
RUN mkdir /app/
WORKDIR /app/

COPY target/QuizBot-1.0-SNAPSHOT.jar /app/QuizBot.jar
COPY quiz_questions.txt /app/

CMD java -jar QuizBot.jar
