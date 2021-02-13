# Implementation documentation

Since the code for the minesweeper game has been offered as a base on this course, a lot of the bots's implementation needs to follow certain guidelines presented in this code. The most important methods are the makeMove and getPossibleMoves methods. The makeMove method returns a move that is implemented on the board. getPossibleMoves method returns a list of possible safe moves. This list is presented when the user clicks the help (bot) button. This feature has not been implemented yet (status on 13.2).

First the player will choose the parameters that will be used in the game (board height, width, amount of mines etc.). Player can choose a difficulty (easy, intermediate, expert) and the parameters will be given automatically to the game. The player can also choose to set a "seed". Using the seed removes any randomness in the mines locations. To game boards will be equal if same seed is given as a parameter in the starting settings.

When the parameters have been given the board will be shown. Before clicking any of the buttons (representing squares on the minefield) the player can choose to use a bot to play the game. The bot will then play the game as long as it can. If the player clicks any buttons on the minedfield, in other words opens a square, then the bot can no longer be used in this game.
