package di.zsanchez31.discordincorporation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

public class DiscordIncorporation extends JavaPlugin {

    private static DiscordIncorporation instance;
    private JDA jda;

    private String version;
    private YamlConfiguration messages;

    private BukkitRunnable autoTask;

    @Override
    public void onEnable() {
        instance = this;

        // Versión del plugin
        version = getDescription().getVersion();

        // Configuración
        saveDefaultConfig();
        saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));

        // Iniciar bot de Discord
        try {
            String token = getConfig().getString("discord.token");
            if (token == null || token.isEmpty()) {
                getLogger().severe("❌ No se encontró el token de Discord en config.yml");
            } else {
                jda = JDABuilder.createDefault(token).build();
                jda.addEventListener(new DiscordListener());

                // Registrar slash command
                jda.updateCommands().addCommands(
                        net.dv8tion.jda.api.interactions.commands.build.Commands
                                .slash("mccommand", "Ejecuta un comando en Minecraft")
                                .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING,
                                        "comando", "El comando de Minecraft a ejecutar", true)
                ).queue();
            }
        } catch (Exception e) {
            getLogger().severe("❌ Error al iniciar el bot de Discord: " + e.getMessage());
        }

        // Ejemplo de tarea repetitiva cada 10 minutos
        autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    getLogger().info("Auto check cada 10 minutos (ejemplo).");
                }
            }
        };
        autoTask.runTaskTimer(this, 0L, 12000L);

        // Registrar comando /reloaddiscord
        this.getCommand("reloaddiscord").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!sender.hasPermission("discordincorporation.reload")) {
                    sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
                    return true;
                }
                reloadPlugin();
                sender.sendMessage(ChatColor.GREEN + "Configuración recargada correctamente.");
                return true;
            }
        });

        // Mensajes de encendido
        sendMessages("plugin.enabled");
    }

    @Override
    public void onDisable() {
        if (autoTask != null) autoTask.cancel();
        if (jda != null) jda.shutdown();

        // Mensajes de apagado
        sendMessages("plugin.disabled");
    }

    public static DiscordIncorporation getInstance() {
        return instance;
    }

    public YamlConfiguration getMessagesConfig() {
        return messages;
    }

    /**
     * Enviar un mensaje a la consola con colores
     */
    public void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "[DiscordIncorporation] " + message.replace("%version%", version)));
    }

    /**
     * Enviar lista de mensajes desde messages.yml
     */
    public void sendMessages(String path) {
        List<String> list = messages.getStringList(path);
        for (String msg : list) {
            sendConsoleMessage(msg);
        }
    }

    /**
     * Recargar config.yml y messages.yml
     */
    public void reloadPlugin() {
        reloadConfig();
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        sendConsoleMessage("&eConfiguración recargada correctamente.");
    }
}
