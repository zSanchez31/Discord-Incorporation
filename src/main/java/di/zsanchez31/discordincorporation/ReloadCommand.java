package di.zsanchez31.discordincorporation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            DiscordIncorporation.getInstance().reloadBotConfig();
            String msg = DiscordIncorporation.getInstance().getMessage("reload-success");
            sender.sendMessage(msg.replace("&", "§"));
            DiscordIncorporation.getInstance().sendConsoleMessage(msg);
            return true;
        }
        sender.sendMessage(DiscordIncorporation.getInstance().getMessage("reload-usage").replace("&", "§"));
        return false;
    }
}
