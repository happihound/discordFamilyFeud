package discordFamilyFued;

public class allowedGuild {

	String serverID;
	String channelID;
	String serverName;

	public allowedGuild(String givenServerID, String givenChannelID) {
		serverID = givenServerID;

		channelID = givenChannelID;
	}

	public allowedGuild(String givenServerID, String givenChannelID, String givenName) {
		serverID = givenServerID;

		channelID = givenChannelID;

		serverName = givenName;

	}

	public String getServerID() {
		return serverID;
	}

	public String getChannelID() {
		return channelID;
	}

	public String getName() {
		return serverName;
	}

}
