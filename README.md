#Bamboo bitbucket pull request builder

[![Build Status](https://travis-ci.org/dkatona/bamboo-bitbucket-pr-builder.svg?branch=master)](https://travis-ci.org/dkatona/bamboo-bitbucket-pr-builder)
[![Coverage Status](https://coveralls.io/repos/github/dkatona/bamboo-bitbucket-pr-builder/badge.svg?branch=master)](https://coveralls.io/github/dkatona/bamboo-bitbucket-pr-builder?branch=master)

Bamboo-bitbucket pull request builder sole purpose is to **trigger builds in bamboo based on comments in pull requests on bitbucket**. 
Unfortunately there is no easy way to bridge these 2 Atlassian products via standard settings either in Bamboo or Bitbucket, that's why I created this app.

After triggering the bamboo build, this app adds a comment to the respective pull request with the link to bamboo and a optional joke
about Chuck Norris :)

##Running

Application is a standard [Spring Boot](http://projects.spring.io/spring-boot/) app and you can configure it as such (for details see below).
It can be run from docker image built on docker hub:

`docker run -v=./config:/opt/pr-builder/config dkatona/bamboo-bitbucket-pr-builder`

Application exposes two endpoints:

* _/commentCreated_ - receiving information about created comment from bitbucket hook and invoking configured bamboo job
* _/healthcheck_ - checks that configuration is set properly

Both endpoints are secured by token you need to supply with every call as a query parameter `accessToken`, value of which can be
configured in the configuration file.

##Configuration

In general, you can configure the app with all means a Spring Boot app can be configured - see this [link](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files).
In the above example of running the app in the docker container, I created config directory with `application.properties` containing custom configuration, but you are
free to supply it on the command line when running the container.

For connection to Bitbucket/Bamboo you can use either OAuth or basic authentication - you need to set `bamboo.basic.*/bamboo.oauth.*` and
`bitbucket.basic.*/bitbucket.oauth.*` properties respectively.

More configuration options with descriptions can be found in [application.properties](src/main/resources/application.properties).

###How to obtain OAuth token to Bitbucket

You need to go to: Profile -> Bitbucket settings -> OAuth -> Add consumer. Don't forget to fill callback URI to arbitrary value and add
pull requests read&write permission. After creating the consumer, just copy clientId and clientSecret and add it to the configuration.

###How to obtain OAuth token to Bamboo

This is a bit complicated as Bamboo uses OAuth1.0a, you need a private/public key pair to sign your requests. Please follow a guide in this
[excellent post](https://www.mibexsoftware.com/blog/how-to-use-oauth-with-atlassian-products/) and obtain apiKey, accessToken and accessTokenSecret
and set them to the configuration file. Don't forget to set a path to your private key (`bamboo.oauth.privateKey`) 
in order to sign requests.

###How to set bitbucket hooks

In order to invoke this application when a pull request is commented, you need to set bitbucket hooks - a guide is available as a part of bitbucket
[knowledgebase](https://confluence.atlassian.com/bitbucket/manage-webhooks-735643732.html). Trigger should be set to `Comment created`.
