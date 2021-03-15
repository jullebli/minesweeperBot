# Documentation for testing

The bots have been tested manually and checked that they function as they should. A lot of prints have been added to help manual testing. Some classes offer methods that help converting the squares and sets to a readable understandable String value.

## Unit test

Due to the randomness in the making of a board, the bots have few Unit tests. My own datastructures (MyList and Myset) are tested extensively except for the performance evaluator. Test coverage can be found in the jacoco report.

## Manual testing

The bots have been tested manually several times. First during creation and adding new operations and also after implementing my own data structures MyList and MySet. 

### Help bot

All of the bots help function has been tested manually. Most of these games were played so that the player made no own desicions but just executing the suggestions made by the bot. Opening all green squares and putting a flag to all red squares. Every game was won when the bot could make suggestions until the end of the game. In these situations that no squares where highlighted after clicking the "help (bot)" button it was examined if there were safe choices left. After finding all the bugs in the code the help bots have been quite accurate. Some of the games where the bot could not give instructions were lost due to a bad random guess. But many were won because human intellect could solve some problems and then the bot could give instructions again. These bots use single point strategies. That means that they make decisions based on information gained from one point, one square in this situation. This 

## Performance testing

I think the major difference in my and Becerra's algorithms win rates is due to the minesweeper game implementation differences. In my base code a safe area is formed surrounding the first opened square. In this safe area there can be no mines. And I think this makes the game easier and the win rates for my bots higher since you get more information by opening a bigger area at first.

|Bot|Difficulty|Win rate|Average game duration|
|-----|------|------|------|
|SinglePointBot|Beginner|91.8%|0.52 ms|
|SinglePointBot|Intermediate|48.8%|2.72 ms|
|SinglePointBot|Expert|2%|4.53 ms|
|DoubleSetSinglePointBot|Beginner|89.1%|0.17 ms|
|DoubleSetSinglePointBot|Intermediate|40.1%|0.43 ms|
|DoubleSetSinglePointBot|Expert|1.3%|0.47 ms|
|NaiveSinglePointBot|Beginner|56.1%|0.28 ms|
|NaiveSinglePointBot|Intermediate|10.1%|0.66 ms|
|NaiveSinglePointBot|Expert|0%|0.47 ms|
|NaiveSinglePointBotUsingLinkedHashSet|Beginner|89.8%|0.21 ms|
|NaiveSinglePointBotUsingLinkedHashSet|Intermediate|45.5%|0.51 ms|
|NaiveSinglePointBotUsingLinkedHashSet|Expert|1.2%|0.5 ms|

The test results in Becerra's thesis:

<img src="https://github.com/jullebli/minesweeperBot/blob/master/documentation/BecerraResults.png" />
