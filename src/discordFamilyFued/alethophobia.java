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

  ArrayList<ServerInstance> servers;

  public alethophobia() {
    servers = new ArrayList<ServerInstance>();
    for (int i = 0; Main.permittedGuilds.length > i; ) {
      allowedGuild guild = null;
      ServerInstance newServer = null;
      guild =
          new allowedGuild(
              Main.permittedGuilds[i], Main.permittedChannels[i], Main.permittedServerNames[i]);
      newServer = new ServerInstance(guild, this);
      Main.Logger.Log(
          "made guild "
              + guild.getName()
              + " guildID:"
              + guild.getServerID()
              + " channelID:"
              + guild.getChannelID());
      try {
        Files.createDirectories(Paths.get(Main.userFileLocation + Main.permittedGuilds[i] + "\\"));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      this.servers.add(newServer);
      i++;
    }
  }

  public void restartServer(ServerInstance server, allowedGuild guild, MessageChannel channel) {
    for (int i = 0; servers.size() > i; ) {
      if (servers.get(i).getID() == server.getID()) {
        Main.Logger.Log("Restarting Server");
        channel.sendMessage("```diff\r\n" + "- PLEASE HOLD RESTARTING SERVER " + "```").queue();
        ServerInstance newServer = null;
        newServer = new ServerInstance(guild, this);
        Main.Logger.Log(
            "remade guild "
                + guild.getName()
                + " guildID:"
                + guild.getServerID()
                + " channelID:"
                + guild.getChannelID());
        try {
          Files.createDirectories(Paths.get(Main.userFileLocation + guild.getServerID() + "\\"));
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        servers.set(i, newServer);
        channel.sendMessage("```diff\r\n" + "- THE SERVER HAS BEEN RESTARTED" + "```").queue();
        return;
      }

      i++;
    }
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    for (int i = 0; servers.size() > i; ) {
      if (event.getGuild().getId().equals(servers.get(i).getID())) {
        servers.get(i).onMessageReceived(event);
        return;
      }
      i++;
    }
  }

  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    for (int i = 0; servers.size() > i; ) {
      if (event.getGuild().getId().equals(servers.get(i).getID())) {
        servers.get(i).onMessageReactionAdd(event);
        return;
      }
      i++;
    }
  }
}
