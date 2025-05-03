To generate and launch allure reports successfully run the following commands one after another at pom.xml location.
Make sure allure is installed in your system at global level, i.e the environment and system path variables should be added.

allure generate allure-results -o target/allure-report --clean
allure open target/allure-report