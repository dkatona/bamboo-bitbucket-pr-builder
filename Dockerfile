FROM maven:onbuild
MAINTAINER Dusan Katona <katonadusan@gmail.com>

RUN mkdir /opt/pr-builder;cp /usr/src/app/target/*.jar /opt/pr-builder

#urandom fix for hanged spring boot process on some systems
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/pr-builder/bamboo-bitbucket-pr-builder-1.0-SNAPSHOT.jar"]
