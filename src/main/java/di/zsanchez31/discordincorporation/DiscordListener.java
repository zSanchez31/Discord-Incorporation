package di.zsanchez31.discordincorporation;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("mccommand")) {
            String comando = event.getOption("comando").getAsString();

            // Roles permitidos desde config.yml
            List<String> allowedRoles = DiscordIncorporation.getInstance()
                    .getConfig()
                    .getStringList("discord.allowed-roles");

            // Usuario y roles en Discord
            Member member = event.getMember();
            if (member == null) {
                event.reply("❌ Error: no se pudo obtener tu información de usuario.")
                        .setEphemeral(true).queue();
                return;
            }

            List<String> userRoles = member.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            boolean permitido = userRoles.stream().anyMatch(allowedRoles::contains);

            if (!permitido) {
                String noPermMsg = DiscordIncorporation.getInstance().getMessagesConfig()
                        .getString("discord.no_permission", "❌ No tienes permiso para ejecutar este comando.");
                event.reply(noPermMsg).setEphemeral(true).queue();
                return;
            }

            // Ejecutar comando en Minecraft
            Bukkit.getScheduler().runTask(DiscordIncorporation.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comando);
            });

            // Mensaje de confirmación
            String msg = DiscordIncorporation.getInstance().getMessagesConfig()
                    .getString("discord.executed", "✅ Ejecutado: %command%")
                    .replace("%command%", comando);

            event.reply(msg).queue();
        }
    }
}
