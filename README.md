# Allure TeamCity Plugin

Allure plugin for Teamcity allows to get [Allure report](http://allure.qatools.ru) as Teamcity build artifact.

![image](https://raw.github.com/allure-framework/allure-core/master/allure-dashboard.png)

## Installation

 * [Download](https://github.com/allure-framework/allure-teamcity-plugin/releases/latest) the latest version of plugin.
 * Shutdown the TeamCity server.
 * Copy the zip archive with the plugin into the [TeamCity Data Directory](http://confluence.jetbrains.com/display/TCD8/TeamCity+Data+Directory)/plugins directory.
 * Start the TeamCity server: the plugin files will be unpacked and processed automatically. The plugin will be available in the Plugins List in the Administration area.

## Configuration

 * Open "Build Configuration settings" > "Build Features"
 * Click "Add build feature" > "Allure Report Generation"

![configuration](https://raw.githubusercontent.com/allure-framework/allure-teamcity-plugin/master/img/allure-configuration.png)

## Usage

When the build is done you will get Allure Report as a part of build artifacts - simply open **index.html**
![report](https://raw.githubusercontent.com/allure-framework/allure-teamcity-plugin/master/img/allure-report.png)

## Contact us
Mailing list: [allure@yandex-team.ru](mailto:allure@yandex-team.ru)
