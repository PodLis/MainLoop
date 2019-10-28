package com.gmail.thewarzealot.mainloop;

import com.gmail.thewarzealot.mainloop.api.*;
import com.gmail.thewarzealot.mainloop.util.ConfigUtilities;
import com.gmail.thewarzealot.mainloop.util.JsonConverter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class MainLoop extends JavaPlugin {

    private char loop_counter;
    private char bossBar_counter;
    private char actionBar_counter;
    private char chatMessage_counter;

    private FileConfiguration configuration;
    private File file;
    private BukkitScheduler scheduler;

    private ArrayList<HubsBar> bossBars;
    private ArrayList<ActionBar> actionBars;
    private ArrayList<ChatMessage> chatMessages;

    private PlayerCommand playerFeedCommand;
    private String consoleFeedCommand;

    @Override
    public void onEnable() {

        loadFiles();

        try {
            Commands cmds = new Commands(this);
            getCommand("mainloop").setExecutor(cmds);
            getCommand("mainloop").setTabCompleter(cmds);

            scheduler = getServer().getScheduler();
            startTask();

            logConsole("Successfully enabled.");
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was an error.");
        }
    }

    @Override
    public void onDisable() {

        try {
            cancelTask();
            logConsole("Successfully disabled.");
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was an error.");
        }

    }

    void startTask() {

        loop_counter = 0;
        bossBar_counter = Character.MAX_VALUE;
        actionBar_counter = Character.MAX_VALUE;
        chatMessage_counter = Character.MAX_VALUE;

        int bossBar_mark = configuration.getInt("boss-bars.delay");
        int actionBar_mark = configuration.getInt("action-bars.delay");
        int chatMessage_mark = configuration.getInt("chat-messages.delay");
        int feed_mark = configuration.getInt("periodic-feed.delay");

        determineAll();

        scheduler.scheduleSyncRepeatingTask(this, () -> {

            if (loop_counter % bossBar_mark == 0) {
                bossBars.get(bossBar_counter % bossBars.size()).clean();
                bossBar_counter++;
                bossBars.get(bossBar_counter % bossBars.size()).send(Bukkit.getOnlinePlayers());
            }
            if (loop_counter % actionBar_mark == 0) {
                actionBar_counter++;
                actionBars.get(actionBar_counter % actionBars.size()).send(Bukkit.getOnlinePlayers());
            }

            if (loop_counter % chatMessage_mark == 0) {
                chatMessage_counter++;
                chatMessages.get(chatMessage_counter % chatMessages.size()).send(Bukkit.getOnlinePlayers());
            }

            if (loop_counter % feed_mark == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleFeedCommand);
                playerFeedCommand.send(Bukkit.getOnlinePlayers());
            }

            loop_counter++;

        }, 0L, 20L);

    }

    void cancelTask() {

        for (HubsBar bar : bossBars) {
            bar.clean();
        }
        scheduler.cancelTasks(this);

    }

    private void determineAll() {

        //feeding
        consoleFeedCommand = configuration.getString("periodic-feed.cmd-to-console");
        playerFeedCommand = new PlayerCommand(configuration.getString("periodic-feed.cmd-to-player"));

        //boss-bars
        String[] bossTexts = ConfigUtilities.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "text");
        String[] bossColors = ConfigUtilities.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "color");
        String[] bossStyles = ConfigUtilities.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "segmented");
        double[] bossProgresses = ConfigUtilities.getDoubles(configuration.getConfigurationSection("boss-bars.bars"), "progress");
        bossBars = new ArrayList<>();
        for (int i = 0; i < bossTexts.length; i++) {
            bossBars.add(new HubsBar(this,
                    replaceSymbolsAndNull(bossTexts[i]),
                    BarColor.valueOf(bossColors[i]),
                    BarStyle.valueOf(bossStyles[i]),
                    bossProgresses[i]));
        }

        //action-bars
        String[] actionTexts = ConfigUtilities.getStrings(configuration.getConfigurationSection("action-bars.bars"), "text");
        actionBars = new ArrayList<>();
        for (String text : actionTexts) {
            actionBars.add(new ActionBar(replaceSymbolsAndNull(text)));
        }

        //chat-messages
        String[][] chatTexts = ConfigUtilities.getArrayOfStrings(configuration.getConfigurationSection("chat-messages.messages"), "text");
        boolean[] chatIsJson = ConfigUtilities.getBooleans(configuration.getConfigurationSection("chat-messages.messages"), "raw");
        JsonConverter.setHoversExecutes(
                ConfigUtilities.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[0],
                ConfigUtilities.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[1],
                ConfigUtilities.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[0],
                ConfigUtilities.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[1]
        );
        chatMessages = new ArrayList<>();
        for (int i = 0; i < chatTexts.length; i++) {
            if (chatIsJson[i]) {

                String[] strings = new String[chatTexts[i].length];
                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = replaceSymbolsAndNull(JsonConverter.getJsonString(chatTexts[i][j]));
                }
                chatMessages.add(new RawMessage(strings)); //Hover-Click-able messages

            } else {

                String[] strings = new String[chatTexts[i].length];
                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = replaceSymbolsAndNull(chatTexts[i][j]);
                }
                chatMessages.add(new ChatMessage(strings)); //Simple-text messages

            }
        }

    }

    void loadFiles() {
        try {
            if (file == null) {
                file = new File(getFolder(), "config.yml");
            }

            if (file.exists()) {
                configuration = YamlConfiguration.loadConfiguration(file);
                configuration.load(file);
                reloadConfig();
            } else {
                saveResource("config.yml", false);
                configuration = YamlConfiguration.loadConfiguration(file);
                logConsole("The 'config.yml' file successfully created!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was a file error.");
        }
    }

    private File getFolder() {
        File folder = getDataFolder();
        if (!folder.exists() && folder.mkdir()) {
            logConsole("Folder recreated");
        }
        return folder;
    }

    void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[MainLoop] " + message);
    }

    private String replaceSymbolsAndNull(String s) {
        return s != null ? s.replace("&", "\u00a7") : "";
    }
}
