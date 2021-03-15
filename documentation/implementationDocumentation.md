# Implementation documentation

## Project structure

The project structure contains mainly code from the base project. Here is a link to the base project [architecture](https://github.com/jullebli/minesweeperBot/blob/master/documentation/gameBaseDocumentation/Architecture-Documentation.md). The base code for example defines the basic data structures used by bots, such as Square, Board and Move.

Since the code for the minesweeper game has been offered as a base on this course, a lot of the bots's implementation needs to follow certain guidelines presented in this code. The most important methods are the makeMove and getPossibleMoves methods defined in the Bot interface. BotExecutor calls the makeMove method of the bot to get the Move to play in Bot Game. getPossibleMoves method returns a list of possible safe moves that are shown to the player if "help (bot)" button is clicked.

In this project I implemented three different bots, two data structures and a performance evaluation tool. 

### AFN and AMN

These two algorithms were used in all of the three bots. AFN and AMN work well with single point algorithms since these algorithms make decisions based on a single point, in my implementations from the perspective of one opened square at a time. AMN and AFN handle information from one opened square's point of view by comparing the square's mine count (tells the amount of mines in the neighbouring squares) and the square's neighbouring squares.

AFN(All Free Neighbours) means a situation where an opened square has as many marked/flagged neighbouring squares as the opened square's mine count. So in this case all of the other unmarked/unflagged unopened neighbouring squares do not contain a mine. These unopened squares are "free" since they do not contain a mine and they are safe to open. So this algorithm is used in finding squares that can be opened in order to open enough squares to win the game.

AMN(All mine/mark neighbours) means a situation where an opened square has the same amount of unopened neighbouring squares as the opened square's mine count. In this case all unopened neighbouring squares contain mines. This algorithm is used in finding mines to mark/flag them. This helps AFN in finding "free" squares.

### SinglePointBot

SinglePointBot was my first implementation of a minesweeper solving bot. This bot's creation was started before I knew I would be implementing the pseudocode algorithms which the other bots are based on. So it is quite different from the other two even though it uses the same AFN and AMN algorithms.

SinglePointBot makes the first move in a predestined square. This could be quite easily modified for testing purposes. This bot uses in Bot Game the same method that is called when the help bot function is used, getPossibleMoves. The second parameter of getPossibleMovesAsMyList is a boolean which determines if it should return all possible moves. Help bot is designed to return all possible moves to help the player as much as it can. makeMove does not want all possible moves since it can only return one move to BotExecutor. If all moves are not wanted then getPossibleMoves returns only flag moves found by AMN. If there are no flag moves then the method tries to find open moves with AFN.

This bot does not save information about the squares between makeMove calls. It uses the information gained by Board and Square classes. When makeMove is called it calls getPossibleMovesAsMyList which returns a list. MakeMove returns the first move on this list. If the list is empty it finds an unopened unflagged random square to return.

### DoubleSetSinglePointBot

DoubleSetSinglePointBot uses two sets to store information about the squares that should be handled: set S for squares that are safe to open and set Q for squares that should be examined again until they are a case of AMN or AFN. Sets "marked" and "opened" are used to keep track of opened and marked squares to handle them more efficiently. Marked set also keeps track of squares to be flagged because the information about to be flagged/flagged squares is needed before the flag moves have been executed. Bot Game and help bot functions are separated: Bot Game uses makeMove method while help bot uses getPossibleMoves method. There is a major difference in their execution. When using help bot the information gained when it was last called is zeroed. Then the board is examined fully and the current information is stored in the sets.

DoubleSetSinglePointBot makes the first move in a predestined square. This bot's Bot game uses the makeMove method to determine which move is made on the board next. The square that has been opened last is saved in a value named prev. First all the pending flag moves are made on the board. Then the bot will check if prev is a case of AFN. If it is then all of its unmarked unopened neightbours are added to S set. If not then prev is added to the Q set. If there are any squares in the S set then one of them is opened. This opened square is set as prev. If the S set had been empty then the Q set's squares will be gone through to determine if the squares are a case of AMN. In the case of AMN the unmarked unopened neighbours of the square will be added to the marked set and to a set keeping information about the squares that will be flagged. Then the Q set's squares will be gone through to determine if the squares are a case of AFN. In the case of AFN the unmarked unopened squares will be added to S set.

### NaiveSinglePointBot

NaiveSinglePointBot is based on Becerra's pseudocode. This bot was implemented most recently and the pseudocode which it was based one was not specific enough so some critical desicions were made in order to get the bot to perform like it should. This bot's performance is affected by the ordering problem that is mentioned in Becerra's text and is not addressed in the pseudocode. The troubles started when LinkedHashSet sets used were replaced by MySet sets. LinkedHashSet sets keep track of the order of the elements in the set but mySet sets do not. So sometimes the squares will be gone through in a non-optimal order.

As in DoubleSetSinglePoint bot the NaiveSinglePointBot's Bot Game and help bot functions are separated: Bot Game uses makeMove method while help bot uses getPossibleMoves method. The difference between these two bots is that naiveSinglePointBot does have a Q set for squares that will be examined again until they are a case of AMN of AFN. This going through squares that have gained more information about their neighbours is replaced by a function that adds the . A difference between the two pseudocode bots is also that NaiveSinglePointBot Bot game's first move is random when the other bot's first move is not random.

### NaiveSinglePointBotUsingLinkedHashSet

This bot is only used for comparing its performance with NaiveSinglePointBot. These two bots are the same except NaiveSinglePointBot uses MySet and NaiveSinglePointBotUsingLinkedHashSet uses LinkedHashSet. LinkedHashSet is a set implementation that keeps track of the order of the elements in the set. LinkedHashSet was used first in all of the bots creation and testing. Since LinkedHashSet has an order the bots made moves in a more deterministic way and the bugs in their functions could be found easier. Only NaiveSinglePointBot seemed to be affected by changing the sets used to MySet sets.

### MyList

This is my own implementation of a list. It implements the methods that are needed by the bots: add, addAll, remove, get, isEmpty and size. It uses an array of elements that is reallocated when the array is full. Currently the remove method does not reallocate the array but this fuctionality could be easily implemented if needed to save space.

The base code for the minesweeper game needs the getPossibleMoves to return an ArrayList. That is why MyList has a method toArrayList which returns the MyList list as an ArrayList list. MyList does not implement the java.util.List interface.

### MySet

This is my own implementation of a hashed set. It implements the methods that are needed by the bots: add, addAll, remove, contains, indexOf, size, isEmpty and iterator. A tombstone value is used in the implementation of MySet. A tombstone is switched in the place of a removed element.

MySet has also a private method reHash that is used to calculate new positions in the array to the elements in set. The elements in the array are reallocated and the array size if changed according to the amount of elements in the array. If more than 0.75% of array has elements and/or tombstones then the array is reallocated. The size of the array is increased if there are more than or equal to 50 % of elements in the array (tombstones are not counted). Tombstones are not reallocated. toString method is used for testing purposes only.

The minesweeper game base's Board class's getOpenSquares returns a Hashset so I made a constructor to MySet that takes HashSet set as a parameter to help in converting a HashSet to MySet.

### BotPerformanceEvaluator

This class is used to test the performance of the bots. It returns a print of a table in markdown form containing information about the performance testing. Information contains data of which bot played the games, what difficulty the game was, the win rate and average game duration. The table can be found in the testingDocumentation.md. GameResult class made by me is used in the gathering of information. Gameresult class saves information about if the game was won and the time used in playing the one game.

## Comparative performance analysis

Performane test document can be found in the testingDocument.md.

Win rate (order from best to worst):
1. SinglePointBot
2. DoubleSetSinglePointBot
3. NaiveSinglePointBotUsingLinkedHashSet
4. NaiveSinglePointBot

Time used to play 1000 games (order from best to worst):
1. DoubleSetSinglePointBot
2. NaiveSinglePointBotUsingLinkedHashSet
3. NaiveSinglePointBot
4. SinglePointBot

## Possible flaws and improvements

I implemented Becerra's pseudocode for Naive Single Point algorithm to the NaiveSinglePointBot and could not figure out how to get it to work efficiently using MySet. Because there was only one set I was struggling because some squares will not be examined again when there are some changes in the neigbouring squares.

### flaws

I could have used the TestApp class. I totally forgot its existence. I could not figure out the ordering problem in the Naive Single Point algorithm and there exits a better implementation of the pseudocode.

### Improvements

- the code could be refactored some more. There are some sections that use same kind of code segments like going through the neighbours of a square.
- bot using CSP algorithm will most definitely perform better. These bots could be improved with improving the guessing so that it is not completely random.
- correct the error in NaiveSingleStepBot. How to go through the already gone through squares before making a random move?
- testing different starting strategies: random or not, which square is the best to start.
- comparing Becerra's results in more detail (average quesses, deviations etc)

## Sources

Becerra, D. J. (2015). Algorithmic approaches to playing minesweeper (Unpublished bachelor's thesis). Harvard College, Cambridge, Massachusetts, United States. Available at <https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf?sequence=1>

