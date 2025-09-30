package di.zsanchez31.discordincorporation;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ConsoleCommandListener implements Listener {

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String output = "[CONSOLE] " + event.getCommand();
        DiscordListener.sendMessageToDiscord(output);
    }
}
