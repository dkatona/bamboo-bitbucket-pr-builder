FROM maven:onbuild
MAINTAINER Dusan Katona <katonadusan@gmail.com>

#RUN git clone https://github.com/dkatona/bamboo-bitbucket-pr-builder.git /tmp/bamboo-bitbucket-pr-builder
#RUN cd /tmp/bamboo-bitbucket-pr-builder;mvn clean install
RUN mkdir /opt/pr-builder;cp /usr/src/app/target/*.jar /opt/pr-builder

ENTRYPOINT ["java", "-jar", "/opt/pr-builder/bamboo-bitbucket-pr-builder-1.0-SNAPSHOT.jar"]
