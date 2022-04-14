package discordFamilyFued;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class alethophobia extends ListenerAdapter {
  LogSystem logger;
  ArrayList<ServerInstance> servers;

  public alethophobia() {
    this.logger = new LogSystem(Main.getRunNumber());
    servers = new ArrayList<ServerInstance>();
    for (int i = 0; Main.permittedGuilds.length > i; ) {
      allowedGuild guild = null;
      ServerInstance newServer = null;
      guild =
          new allowedGuild(
              Main.permittedGuilds[i], Main.permittedChannels[i], Main.permittedServerNames[i]);
      newServer = new ServerInstance(guild, this);
      logger.Log(
          "made guild "
              + guild.getName()
              + " guildID:"
              + guild.getServerID()
              + " channelID:"
              + guild.getChannelID());
      try {
        Files.createDirectories(Paths.get(Main.userFileLocation + Main.permittedGuilds[i] + "\\"));
      } catch (IOException e) {
        logger.warn(1);
        logger.Log(e.toString());
      }
      this.servers.add(newServer);
      i++;
    }
  }

  public void restartServer(ServerInstance server, MessageChannel channel) {
    for (int i = 0; servers.size() > i; ) {
      if (servers.get(i).getID() == server.getID()) {
        logger.Log("Trying to restart Server: " + server.getID() + "\" " + server.getName() + "\"");
        allowedGuild guild = null;
        ServerInstance newServer = null;
        guild =
            new allowedGuild(
                server.getID(), Main.permittedChannels[i], Main.permittedServerNames[i]);
        newServer = new ServerInstance(guild, this);
        logger.Log(
            "made guild "
                + guild.getName()
                + " guildID:"
                + guild.getServerID()
                + " channelID:"
                + guild.getChannelID());
        try {
          Files.createDirectories(
              Paths.get(Main.userFileLocation + Main.permittedGuilds[i] + "\\"));
        } catch (IOException e) {
          logger.warn(1);
          logger.Log(e.toString());
        }
        servers.set(i, newServer);
        channel
            .sendMessage(
                "```diff\r\n"
                    + "- The server \""
                    + server.getName()
                    + "\" has been restarted"
                    + "```")
            .queue();
        logger.Log("Successfully restarted server");
        return;
      }

      i++;
    }
  }

  public void stopServer(ServerInstance server, allowedGuild guild, MessageChannel channel) {
    for (int i = 0; servers.size() > i; ) {
      if (servers.get(i).getID() == server.getID()) {
        channel
            .sendMessage("```diff\r\n" + "- Sucessfully left server: " + server.getName() + "```")
            .queue();
        servers.remove(i);
        logger.Log("Successfully stopped server: " + server.getName() + " ID: " + server.getID());
        return;
      }
      i++;
    }
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    for (int i = 0; servers.size() > i; ) {
      if (event.getGuild().getIdLong() == (servers.get(i).getID())) {
        servers.get(i).onMessageReceived(event);
        return;
      }
      i++;
    }
  }

  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    for (int i = 0; servers.size() > i; ) {
      if (event.getGuild().getIdLong() == (servers.get(i).getID())) {
        servers.get(i).onMessageReactionAdd(event);
        return;
      }
      i++;
    }
  }
}
