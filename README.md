Allure plugin for Teamcity allows to get Allure report as Teamcity build artifact.

## Installation

 * [Download](https://github.com/allure-framework/allure-teamcity-plugin/releases/latest) latest version of plugin.
 * Shutdown the TeamCity server.
 * Copy the zip archive with the plugin into the [TeamCity Data Directory](http://confluence.jetbrains.com/display/TCD8/TeamCity+Data+Directory)/plugins directory.
 * Start the TeamCity server: the plugin files will be unpacked and processed automatically. The plugin will be available in the Plugins List in the Administration area.

## Configuration

 * Open "Build Configuration settings" > "Build Features"
 * Click "Add build feature" > "Allure Report Generation"

## Usage

When the build is done you will get Allure Report as part of build artifacts - simply open **index.html**:
