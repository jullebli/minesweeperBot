# Week 4 report

Hours used this week: 4

This week I had a meeting with a teacher and I got a more clear vision of what I need to do. I won't do the CSP algorithm. I will be comparing the naive single point algorithm and the double set single point algorithm with improved guessing. I need to test which starting patterns are the best. In this implementation of minesweeper, after opening the first square the game makes a safe area around the square(there will be no mines in a 3x3 area. The first opened square being the middle square of the safe area). In Becerra's thesis and in another source it was tested that the best place to start a game is opening one of the corners. But I don't think that in those minesweeper implementations there was this safe area so I need to test if the corners are the best place to start.

I spent some time testing but next I need to make a board "by hand" so that I do not get a random board every time. I also need to figure out how to implement the "help" function of the SecondBot.
