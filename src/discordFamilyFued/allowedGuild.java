package discordFamilyFued;

public class allowedGuild {

  long serverID;
  long channelID;
  String serverName;

  public allowedGuild(long givenServerID, long givenChannelID) {
    serverID = givenServerID;

    channelID = givenChannelID;
  }

  public allowedGuild(long givenServerID, long givenChannelID, String givenName) {
    serverID = givenServerID;

    channelID = givenChannelID;

    serverName = givenName;
  }

  public long getServerID() {
    return serverID;
  }

  public long getChannelID() {
    return channelID;
  }

  public String getName() {
    return serverName;
  }
}
