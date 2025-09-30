package di.zsanchez31.discordincorporation;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MinecraftChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String format = DiscordIncorporation.getInstance().getMessage("chat-mc-format")
                .replace("%player%", event.getPlayer().getName())
                .replace("%message%", event.getMessage());

        DiscordListener.sendMessageToDiscord(format.replace("&", "§"));
    }
}
