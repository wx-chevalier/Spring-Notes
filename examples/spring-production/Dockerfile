FROM gradle:5.4.1-jdk8 as builder

USER root

COPY . /usr/src/
WORKDIR /usr/src/

RUN gradle --no-daemon spotlessApply && gradle build -x test --no-daemon --console plain

FROM picoded/ubuntu-openjdk-8-jdk

WORKDIR /usr/src/
COPY sbp-deploy /usr/src/
RUN chmod 777 /usr/src/cp.sh
COPY --from=builder /usr/src/build/libs/wsat-agent-0.0.1-SNAPSHOT.jar /usr/src/wsat-agent.jar

ENTRYPOINT ["./cp.sh"]