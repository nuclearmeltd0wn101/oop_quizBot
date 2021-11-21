# Deployment via Docker
1) Build package via Maven: 
`mvn clean package`
2) Build docker image:
`docker build .`
After that, you'll get something like
`Successfully built <Image ID here>`
in an output. Use obtained Image ID in the next step.
3) Run docker container:
`docker run -d -e tgBotToken="<Your bot token here>" --name=QuizBot --restart=unless-stopped <Image ID here>`
