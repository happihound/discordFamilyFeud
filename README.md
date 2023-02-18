# Discord Bot Development
This is a Java-based Discord bot project that was created with the intention of providing real-world examples. The bot performs 3 tasks - engagement, scale and versatility.

# Task 1: Engagement
The first task for the bot was to engage users and encourage them to play games. To achieve this, the bot rewards points to players based on their performance. These points are persistent and are not lost when the bot crashes or restarts. This encourages players to keep playing and accomplish more in the games.

# Task 2: Scale
The second task for the bot was to be scalable. The bot needed to be able to play multiple games on multiple servers asynchronously. If a game crashed on one server, the other games needed to remain unaffected. To achieve this, the bot was implemented with a system that allocates points based on the player's choice. The games are played in 3 rounds, and the points for each round are totaled and saved to a text file with the user's name. The text files are saved by server name and channel, ensuring that the bot will not edit a user's points in the event that they play another game on another server.

# Task 3: Versatility
The final task for the bot was to be versatile. The framework had to be modular and flexible, to make it easy to adjust or fabricate a new game entirely. The game is fully self-contained in its class, and the scalability portion of the server system is independent of the game. This makes it possible to scale a bot that does anything, not just games. The bot is designed to be looked at and understood, but not necessarily installed.

# Conclusion
This Discord bot project is an excellent example of how to develop real-world examples using Java. It demonstrates the importance of engagement, scalability, and versatility, and shows how to tackle these tasks effectively. This project is meant to be looked at and studied, and to serve as a model for other developers who are looking to develop Discord bots.
