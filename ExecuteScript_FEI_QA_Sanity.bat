set currentpath="%cd%"
set PATH=%Path%;%currentpath%\apache-maven-3.5.0\bin
set PATH=%Path%;%currentpath%\allure-2.7.0\bin
call mvn clean
call mvn test -Dcucumber.Options="--plugin ru.yandex.qatools.allure.cucumberjvm.AllureReporter --tags @Sanity_QA"
call allure serve target\allure-results
