# Minesweeper bot

A minesweeper solver/helper made in the course DATA STRUCTURES PROJECT (4 CR). The implementation code of the minesweeper game has not been made by me. It was offered as a base to help develope the solver/helper and has been made by other students on another course ([link to the git repository of the base material](https://github.com/TiraLabra/minesweeper.git)). 

My work/code consists of the bot imlementations: SinglePointBot, DoubleSetSinglePointBot and NaiveSinglePointBot and their tests. Also classes in the minesweeper.util package (MyList, MySet and MySetIterator) and their tests are made by me.

I recommend copying the git repository and then running the program from terminal or in Netbeans.

## Documentation

- [Project specification](https://github.com/jullebli/minesweeperBot/blob/master/documentation/project-specification.md)
- [Testing documentation](https://github.com/jullebli/minesweeperBot/blob/master/documentation/testingDocumentation.md)
- [Implementation documentation](https://github.com/jullebli/minesweeperBot/blob/master/documentation/implementationDocumentation.md)
- [User guide](https://github.com/jullebli/minesweeperBot/blob/master/documentation/userGuide.md)

## Weekly reports

- [Week 1 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week1report.md)
- [Week 2 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week2report.md)
- [Week 3 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week3report.md)
- [Week 4 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week4report.md)
- [Week 5 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week5report.md)
- [Week 6 report](https://github.com/jullebli/minesweeperBot/blob/master/documentation/week6report.md)

## Command line commands:

#### Build

./gradlew build

sometimes some tests are missing from jacoco report even though the jacoco command is run. The missing test results might show if you build the project before running jacoco command.

#### Run: 

./gradlew run

if you have problems with this command, try: ./gradlew jar && java -cp build/libs/minesweeper.jar:/usr/share/java/javafx-base.jar:/usr/share/java/javafx-controls.jar:/usr/share/java/javafx-graphics-11.jar minesweeper.AppLauncher

#### Test:

./gradlew test

#### Jacoco report:

./gradlew jacocoTestReport

The report will be available at build/reports/jacoco/test/html/index.html

#### Checkstyle:

./gradlew check

The report will be available at build/reports/checkstyle/main.html

#### JavaDoc:

./gradlew javadoc

The Javadoc will be available at build/docs/javadoc/index.html

#### Performance test:

./gradlew -PmainClass=BotPerformanceEvaluator  performanceTest

the results will be given as a markdown table in the terminal
