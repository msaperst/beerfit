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
./gradlew test
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
