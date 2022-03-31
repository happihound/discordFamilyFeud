package discordFamilyFued;

import java.util.ArrayList;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class alethophobia extends ListenerAdapter {

	ArrayList<ServerInstance> servers;

	public alethophobia(ArrayList<ServerInstance> givenServers) {
		servers = givenServers;

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		for (int i = 0; servers.size() > i;) {
			if (event.getGuild().getId().equals(servers.get(i).getID())) {
				servers.get(i).onMessageReceived(event);
				return;
			}
			i++;
		}

	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		for (int i = 0; servers.size() > i;) {
			if (event.getGuild().getId() == servers.get(i).getID()) {
				servers.get(i).onMessageReactionAdd(event);
				return;
			}
			i++;
		}

	}

}
