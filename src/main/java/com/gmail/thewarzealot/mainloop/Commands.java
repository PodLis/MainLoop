package com.gmail.thewarzealot.mainloop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Commands implements CommandExecutor, TabCompleter {

    private MainLoop plugin;

    Commands(MainLoop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("mainloop")) {

                if (args.length == 0)
                {
                    sender.sendMessage(replaceColor("&e&l[&3&lMain&a&lLoop&b&l Info&e&l]"));
                    sender.sendMessage(replaceColor("&5Version:&a " + plugin.getDescription().getVersion()));
                    sender.sendMessage(replaceColor("&5Author, created by:&a Rosenboum"));
                }

                else if (args[0].equalsIgnoreCase("test"))
                {
                    if (sender instanceof Player && !sender.hasPermission(Permissions.Perm.TEST.getPerm())) {
                        sender.sendMessage(replaceColor("&e&l[&3&lMain&a&lLoop&e&l]&r You have no permissions to use&6 " + args[0]));
                        return true;
                    }
                    sender.sendMessage(replaceColor("&5Trolling by&a Rosenboum"));
                    return true;
                }

                else if (args[0].equalsIgnoreCase("reload"))
                {
                    if (sender instanceof Player && !sender.hasPermission(Permissions.Perm.RELOAD.getPerm())) {
                        sender.sendMessage(replaceColor("&e&l[&3&lMain&a&lLoop&e&l]&r You have no permissions to use&6 " + args[0]));
                        return true;
                    }

                    plugin.cancelTask();
                    plugin.loadFiles();
                    plugin.startTask();

                    sender.sendMessage(replaceColor("&e&l[&3&lMain&a&lLoop&e&l]&r reload config.."));

                    return true;
                }

                else {
                    sender.sendMessage(replaceColor("&e&l[&3&lMain&a&lLoop&e&l]&r unknown sub-command&6 " + args[0]));
                    return true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            plugin.logConsole(Level.WARNING, "Some troubles with commands.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;

        if (args.length == 1) {
            List<String> cmds = new ArrayList<>(getCmds(sender));
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

    private List<String> getCmds(CommandSender sender) {
        List<String> c = new ArrayList<>();
        for (String cmd : Arrays.asList("test", "reload")) {
            if (!sender.hasPermission("mainloop." + cmd)) {
                continue;
            }

            c.add(cmd);
        }
        return c;
    }

    private String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }
}
