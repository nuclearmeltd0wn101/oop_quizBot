FROM openjdk:17-alpine3.14

# paste your token here
ENV tgBotToken=""
ENV dbPath=/storage/QuizBot.sqlite

VOLUME /storage/
RUN mkdir /app/
WORKDIR /app/

COPY out/artifacts/QuizBot_jar/QuizBot.jar /app/
COPY quiz_questions.txt /app/

CMD java -jar QuizBot.jar

LABEL name="Telegram Quiz bot"
