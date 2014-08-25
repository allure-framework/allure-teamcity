# Allure TeamCity Plugin

This repository contains Allure plugin for [Teamcity](http://www.jetbrains.com/teamcity/) allowing to generate [Allure report](http://allure.qatools.ru) from [existing Allure XML files](https://github.com/allure-framework/allure-core/wiki#gathering-information-about-tests) as Teamcity build artifact.

![image](https://raw.github.com/allure-framework/allure-core/master/allure-dashboard.png)

## Installation

 1. [Download](https://github.com/allure-framework/allure-teamcity-plugin/releases/latest) the latest version of plugin.
 2. Shutdown the TeamCity server.
 3. Copy the zip archive with the plugin into the [TeamCity Data Directory](http://confluence.jetbrains.com/display/TCD8/TeamCity+Data+Directory)/plugins directory.
 4. Start TeamCity server: all plugin files will be unpacked and processed automatically. Plugin will be available in the Plugins List in the Administration area.

## Configuration

 1. Open **Build Configuration Settings**
 2. Ensure that your build [generates Allure XML files](https://github.com/allure-framework/allure-core/wiki#gathering-information-about-tests)
 3. Clock **Build Features > Add Build Feature > Allure Report Generation**

![configuration](https://raw.githubusercontent.com/allure-framework/allure-teamcity-plugin/master/img/allure-configuration.png)

## Usage

When the build is done you will get Allure Report as a part of build artifacts - simply open **index.html**
![report](https://raw.githubusercontent.com/allure-framework/allure-teamcity-plugin/master/img/allure-report.png)

## Contact us
Mailing list: [allure@yandex-team.ru](mailto:allure@yandex-team.ru)
