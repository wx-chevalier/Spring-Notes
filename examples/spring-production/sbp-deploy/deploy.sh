#!/bin/bash
PROJECT="sbpnt"

git pull

echo 'start build java'
./gradlew spotlessApply
./gradlew build -x test

echo 'start build docker image.'
docker build -t ${PROJECT}:latest -f ./deploy/Dockerfile .

echo 'stop and remove the current container.'
docker container stop ${PROJECT}
docker container rm ${PROJECT}

echo 'run a new container.'
docker run -d --restart always -p 9000:9000 --name ${PROJECT} ${PROJECT}:latest
