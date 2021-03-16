# User Guide

### Some game functions to take into account while playing

The game is set so that player cannot step on a mine when opening the first square. There is also a safe area around the first opened square. No mines will be in the neighbouring squares of the first opened square. The game is lost when player opens a square containing a mine. The game is won if all the squares that do not contain a mine are opened and only the amount of mines of squares are unopened.

### Starting a new game

First the player will choose the parameters that will be used in the game (board height and width, amount of mines etc.). Player can choose a difficulty (easy, intermediate, expert) and parameters will be given automatically to the game. Player can also give these parameters by clicking the "Set Custom Board" button. 

Player can also choose to use a "seed". Using a seed removes any randomness in the mine locations. However this only works when the first opened square is the same since the mines are put in place after opening the first square. Game boards will be equal if same seed and same parameters are given in the starting settings and the first opened square is the same.

### Helpbot

You can use the help from the bot when you play the game. Clicking the "help (bot)" button will give you some suggestions on which squares contain a mine and which don't. Those squares that the bot thinks do not contain a mine are highlighted with green. Those squares that the bot thinks contain a mine are highlighted with red. <b>Bear in mind that the bot will use the locations of the flags on the board in making its suggestions.</b> The bot will not make suggestions to opened or flagged squares but will make decisions based on them. So if a wrong square is flagged, it will affect the suggestions received from the bot. If the bot has no suggestions then no highlights will be shown after clicking the button.

##### Highlights:
- green: safe to open
- red: put a flag on this square, it contains a mine
- no highlights: bot cannot give help in this situation

### Bot play

In order to have the bot to play the game you need to click "Bot Game" button before opening any squares. Once the Bot Game is on you cannot participate in playing anymore. If you want to stop the Bot Game then start a new game by clicking the "New Game" button. You can watch the bot play the game until it wins or loses. You can change the speed of the bot making moves by using the slider near the bottom edge. The speed of the shown play does not fully represent the speed of the bot.

#### Changing the bot used in the Bot Game

The game will use the DoubleSetSinglePointBot as default bot. The bot used can be changed from BotSelect class in getBot method. The method should contain one of the lines below:

- return new SinglePointBot();
- return new NaiveSinglePointBot();
- return new NaiveSinglePointBotUsingLinkedHashSet();
- return new DoubleSetSinglePointBot();

These lines are in the method as comments, just change the commented parts accordingly.
