FROM picoded/ubuntu-openjdk-8-jdk

COPY ./build/libs/wsat-agent-0.0.1-SNAPSHOT.jar /usr/src/app.jar
COPY ./deploy/config.json /etc/wsat/config.json

ENTRYPOINT ["java","-jar","/usr/src/app.jar","wx.Application"]