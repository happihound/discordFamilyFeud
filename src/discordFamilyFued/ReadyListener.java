package discordFamilyFued;

import java.util.EventListener;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;

public class ReadyListener implements EventListener {
	public static void main(String[] args) throws LoginException {
		JDA jda = JDABuilder.createDefault(args[0]).addEventListeners(new ReadyListener()).build();
	}

	public void onEvent(GenericEvent event) {
		if (event instanceof ReadyEvent)
			System.out.println("API is ready!");
	}
}