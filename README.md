# beerfit
android app for tracking/mapping working out and drinking

## Building
To create the APK, simply run:
```cmd
./gradlew clean build
```
This will spit out two APKs, a debug and unsigned release version. These can both be found at:
`app/build/outputs/apk`

This will also run the unit tests with code coverage.
JUnit results can be found at:
`app/build/reports/tests`
and the jacoco results can be found at:
`app/build/reports/jacoco`

### Testing
There are additional tests that could/should be run, beyond the unit tests.
These tests each require an emulator or device running/plugged into your test machine.

#### Unit
If you want to just run the unit tests, run the below command:
```cmd
./gradlew jacocoTestReport
```
JUnit results can be found at:
`app/build/reports/tests`
and the jacoco results can be found at:
`app/build/reports/jacoco`

#### Integration
Instrumented integration tests exist. They can be run with the below command:
```cmd
./gradlew connectedAndroidTest
```
Junit results can be found at:
`app/build/reports/androidTests/connected/index.html`

#### Appium
UI Appium tests exist. They can be run with the below command:
```cmd
./gradlew test -Pappium=true
```
JUnit results can be found at:
`app/build/reports/tests`
FAST results can be found at:
`app/build/reports/tests/index.html`

### Features
Below is some information about the features in the BeerFit app

#### Import / Export
In order to preserve, backup, or bulk edit your activities, or other items, an import/export
feature exists. To export your data (backup), in the options menu, click the `Export Data` menu
item. You'll need to give the app permissions to your shared media in or to save the data. All
data will saved in the `/sdcard/BeerFit` folder. A CSV file will be created for each dataset present
in the app. **Note that all prior data in the folder will be overwritten by the export.**

In order to bulk modify the data in the app, you can import from a CSV file as well. Any files in the
`/sdcard/BeerFit/` folder with a name matching a dataset can be selected. **Note that any imported 
datasets will overwrite the existing data in the app.**