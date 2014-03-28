# Allure Report Teamcity Plugin
This repository contains plugin for Teamcity CI server which allows you to generate Allure Report as build artifact.

## Building
In order to build the plugin from source you need to install Apache Maven and any JDK 1.7. When done run the following command from the sources directory:
```
$ mvn clean package
```
When build is finished you will find **allure-plugin.zip** file in the **target/** directory.

## Installing
Install the plugin as usually:
 * Copy zip archive to Teamcity **plugins/** directory.
 * Restart Teamcity.
