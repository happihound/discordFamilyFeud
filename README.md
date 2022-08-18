# discordFamilyFeud
The creation of the Discord Bot was my second development experience with real-world ramifications.
Similar to the Twitch bot, the Discord bot had to perform 3 tasks. The first was engagement. 
The users had to enjoy the game and feel like they were earning something.
The second was scale. I wanted the bot to play multiple games, on multiple servers asynchronously, i.e. if one game crashed on one server,
the other games would remain unaffected. The third was versatility. 
I wanted to make a platform on which it would be relatively easy to adjust or fabricate a new game entirely.  
To tackle the first part, I made the game reward points to each player based on their performance.
These points had to be persistent between games played, bot crashes, and restarts.
These points should make the users feel like they accomplished something and encourage more games to be played.   



To address part two, and keep interest, I implemented a system in which based on the choice the player chose, they gained a certain number of points.
Each game has 3 rounds, and the points for each proceeding round are totaled and saved to a text file with the users name.
These text files were saved first by server name, and then inside by channel.
This ensures the bot won't edit a users points in the event they play another game on another server.
All points and games are entirely sandboxed, even if one game full crashes, all the other games remain entirely unimpacted.
The games take up a very small amount of processing power which allows for scaling at a massive level.   



The final part was the hardest to accomplish.
I wanted to avoid hard-coding nearly anything into the game.
Making a fully scalable framework was challenging, but I managed to pull it off using a very modular/OOB-based system. 
The game is fully self-contained in its class, and the scalability portion of the server system is independent of the game,
so you can massively scale a bot that does anything, not necessarily just games. 
