package di.zsanchez31.discordincorporation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DiscordIncorporation extends JavaPlugin {

    private static DiscordIncorporation instance;
    private JDA jda;
    private String logChannelId;
    private FileConfiguration messages;

    public static DiscordIncorporation getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        String token = config.getString("bot-token");
        logChannelId = config.getString("log-channel-id");

        // Load messages.yml
        saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));

        try {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new DiscordListener())
                    .build();
            jda.awaitReady();
        } catch (InterruptedException e) {
            getLogger().severe("Failed to start Discord bot: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Discord slash commands
        jda.updateCommands().addCommands(
                Commands.slash("online", "Shows online players"),
                Commands.slash("cmd", "Execute a server command").addOption(
                        net.dv8tion.jda.api.interactions.commands.OptionType.STRING,
                        "command",
                        "Command to execute",
                        true
                )
        ).queue();

        // Listeners
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsoleCommandListener(), this);

        getCommand("discordinc").setExecutor(new ReloadCommand());

        // Messages on enable
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-enabled-es").replace("%version%", getDescription().getVersion()));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-enabled-thanks-es"));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-enabled-en").replace("%version%", getDescription().getVersion()));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-enabled-thanks-en"));
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.shutdownNow();
        }
        // Messages on disable
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-disabled-es").replace("%version%", getDescription().getVersion()));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-disabled-thanks-es"));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-disabled-en").replace("%version%", getDescription().getVersion()));
        sendConsoleMessage("[DiscordIncorporation] " + getMessage("plugin-disabled-thanks-en"));
    }

    public JDA getJda() {
        return jda;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public void reloadBotConfig() {
        reloadConfig();
        this.logChannelId = getConfig().getString("log-channel-id");
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
    }

    public String getMessage(String path) {
        return messages.getString(path, "&cMissing message in messages.yml (" + path + ")");
    }

    public void sendConsoleMessage(String msg) {
        getServer().getConsoleSender().sendMessage(msg.replace("&", "§"));
    }
}
