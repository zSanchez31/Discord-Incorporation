package di.zsanchez31.discordincorporation;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String logChannelId = DiscordIncorporation.getInstance().getLogChannelId();
        if (event.getChannel().getId().equals(logChannelId)) {
            String format = DiscordIncorporation.getInstance().getMessage("chat-discord-format")
                    .replace("%user%", event.getAuthor().getName())
                    .replace("%message%", event.getMessage().getContentDisplay());

            Bukkit.getScheduler().runTask(DiscordIncorporation.getInstance(), () ->
                    Bukkit.broadcastMessage(format.replace("&", "§"))
            );
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (name.equals("online")) {
            List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            String message = onlinePlayers.isEmpty() ? "No players online" :
                    "Online players: " + String.join(", ", onlinePlayers);
            event.reply(message).queue();
        } else if (name.equals("cmd")) {
            String comando = event.getOption("command").getAsString();

            Member member = event.getMember();
            List<String> allowedRoles = DiscordIncorporation.getInstance().getConfig().getStringList("allowed-roles");

            boolean permitido = member.getRoles().stream()
                    .map(r -> r.getName())
                    .anyMatch(allowedRoles::contains);

            if (!permitido) {
                event.reply(DiscordIncorporation.getInstance().getMessage("no-permission").replace("&", "§"))
                        .setEphemeral(true)
                        .queue();
                return;
            }

            Bukkit.getScheduler().runTask(DiscordIncorporation.getInstance(), () -> {
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comando);
                String replyMsg = success ? "✅ Command executed: `" + comando + "`" :
                        "❌ Error executing command: `" + comando + "`";
                event.reply(replyMsg).queue();
            });
        }
    }

    public static void sendMessageToDiscord(String content) {
        String logChannelId = DiscordIncorporation.getInstance().getLogChannelId();
        TextChannel channel = DiscordIncorporation.getInstance().getJda().getTextChannelById(logChannelId);
        if (channel != null) {
            channel.sendMessage(content).queue();
        }
    }
}
