# Project specification: Minesweeper solver/helper

This project is a part of the course Data Structures Project in my Bachelor's in Computer Science studies. English is the language that will be used throughrough the project: documentation, code, comments etc. will be in english.

The code of the game has been made by other students and is offered as a base for this course's students for making a minesweeper solver/helper.

### The algorithms that I will use in this project:
 -Algorithm for finding mines and flagging them according to opened squares with numbers
 -Algorithm for finding squares that are safe to open according to flags and opened squares with numbers
 -backtracking search: finding squares with the smallest probability if containing a mine by going through all the possible variations to place mines (according to flags and numbered neighbouring squares). The variations will be split into subsets (if there are regions that are disjoint) if possible.
 
 I chose the first two algorithms based on my own playing experiences. The backtracking search was chosen because it seems to fit in this scenario and has been used by many others after researching the topic. I need to solve problems about determing where the mines are according to opened neighbouring squares's numbers. I need to solve the problem of which squares are safe to open (have no mine). And when I can't use the first two algorithms then I need to solve the problem of comparing different alternatives of mine locations and finding safest square to open. Safest square is the one with lowest possibility of having the mine among alternatives. I need to solve the problem what to do when I don't have enough information. The game cannot be always won since sometimes you just don't have enough information about the squares and then you just need to guess.
 
### How to use the solver/helper

First you need to decide game difficulty: beginner, intermediate or expert. You can also make a custom board and choose the size of the board and amount of mines. If you want the minesweeper solver to play the game, you can choose the solver only when all of the squares are unopened. 

You can use the helper when you play the game yourself. The helper will make suggestions of squares to flagg or squares that are safe to open. The helper takes in consideration the flagged squares when it gives suggestions so if you have flagged a wrong square then the helper might give wrong suggestions because of that.

### Sources

I will be using as a source a post by Luckytoilet on "Lucky's notes" website (https://luckytoilet.wordpress.com/2012/12/23/2125/) and his github project MSolver (https://github.com/luckytoilet/MSolver).

### Expected time and space complexities of the programme


