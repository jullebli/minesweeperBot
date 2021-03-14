# Project specification: Minesweeper solver/helper

This project is a part of the course Data Structures Project in my Bachelor's in Computer Science studies. English is the language that will be used throughrough the project: documentation, code, comments etc. will be in english.

The code of the game has been made by other students and is offered as a base for this course's students for making a minesweeper solver/helper. My code are the classes SinglePointBot, NaivesinglePointBot and DoubleSetSinglePointBot and their tests. So all the other bots than TestBot. There will also be some data structures made by myself like list and set.

### The algorithms that I will use in this project:
- AMN (All mine/marked neighbours): Algorithm for finding mines and flagging them according to opened squares with numbers
- AFN (All free neighbours): Algorithm for finding squares that are safe to open according to flags and opened squares with numbers

Using those two I will implement two algorithms based on Becerra's pseudocode:

- NaiveSinglePoint
- DoubleSetSinglePoint
 
 I chose the first two algorithms based on my own playing experiences. From Becerra's thesis I found a name for them. I discarded the idea of using a CSP algorithm due to lack of time and skills. First I discarded the idea of using backtracking since the teacher said that using it does not really fulfill the requirements of the project in this course. Due to lack of time and choice of the algorithms I did not implement the "safest choice" to the bots. They choose a square to open random when there are no safe choices like in Becerra's pseudocode algorithms.
 
### How to use the solver/helper

First you need to decide game difficulty: beginner, intermediate or expert. You can also make a custom board and choose the size of the board and amount of mines. If you want the minesweeper solver to play the game, you can choose the solver only when all of the squares are unopened. 

You can use the helper when you play the game yourself. The helper will make suggestions of squares that are safe to open. The helper takes in consideration the flagged squares when it gives suggestions so if you have flagged a wrong square then the helper might give wrong suggestions because of that.

The game will use one bot as the default bot. The bot used can be changed from BotSelect class in getBot method. 

### Sources

(Luckytoilet (2012). How to Write your own Minesweeper AI. A website post. Lucky's notes. Available at <https://luckytoilet.wordpress.com/2012/12/23/2125/>.)

Becerra, D. J. (2015). Algorithmic approaches to playing minesweeper (Unpublished bachelor's thesis). Harvard College, Cambridge, Massachusetts, United States. Available at <https://dash.harvard.edu/bitstream/handle/1/14398552/BECERRA-SENIORTHESIS-2015.pdf?sequence=1>

### Expected time and space complexities of the programme

Space: O(mn) where m is height and n is width of the board. Because there will be m x n amount of squares in the minefield.
Time:
