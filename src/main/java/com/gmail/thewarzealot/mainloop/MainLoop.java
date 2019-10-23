package com.gmail.thewarzealot.mainloop;

import com.gmail.thewarzealot.mainloop.api.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.logging.Level;

public class MainLoop extends JavaPlugin {

    private char loop_counter;
    private char bossBar_counter;
    private char actionBar_counter;
    private char chatMessage_counter;

    private FileConfiguration config;
    private File file;
    private BukkitScheduler scheduler;

    private BossBar[] bossBars;
    private ActionBar[] actionBars;
    private String[] chatMessages;

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

        int bossBar_mark = config.getInt("boss-bars.delay");
        int actionBar_mark = config.getInt("action-bars.delay");
        int chatMessage_mark = config.getInt("chat-messages.delay");
        int feed_mark = config.getInt("periodic-feed.delay");

        String console_command = config.getString("periodic-feed.cmd-to-console");
        String player_command = config.getString("periodic-feed.cmd-to-player");

        determineBarsAndMessages();

        scheduler.scheduleSyncRepeatingTask(this, () -> {

            if (loop_counter % bossBar_mark == 0) {
                bossBars[bossBar_counter % bossBars.length].removeAll();
                bossBar_counter++;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    bossBars[bossBar_counter % bossBars.length].addPlayer(p);
                }
            }
            if (loop_counter % actionBar_mark == 0) {
                actionBar_counter++;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    actionBars[actionBar_counter % actionBars.length].send(p);
                }
            }

            if (loop_counter % chatMessage_mark == 0) {
                chatMessage_counter++;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(chatMessages[chatMessage_counter % actionBars.length]);
                }
            }

            if (loop_counter % feed_mark == 0) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), console_command);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.performCommand(player_command);
                }
            }

            loop_counter++;

        }, 0L, 20L);
    }

    void cancelTask() {
        for (BossBar bar : bossBars) {
            bar.removeAll();
        }
        scheduler.cancelTasks(this);
    }

    private void determineBarsAndMessages() {
        bossBars = new BossBar[config.getInt("boss-bars.number-of")];
        actionBars = new ActionBar[config.getInt("action-bars.number-of")];
        chatMessages = new String[config.getInt("chat-messages.number-of")];
        for (int i = 1; i <= bossBars.length; i++) {
            bossBars[i-1] = getServer().createBossBar(
                    replaceSymbolsAndNull(config.getString("boss-bars." + i + ".text")),
                    BarColor.valueOf(config.getString("boss-bars." + i + ".color")),
                    BarStyle.valueOf(config.getString("boss-bars." + i + ".segmented")));
            bossBars[i-1].setProgress(config.getDouble("boss-bars." + i + ".progress"));
        }
        for (int i = 1; i <= actionBars.length; i++) {
            actionBars[i-1] = new ActionBar(replaceSymbolsAndNull(config.getString("action-bars." + i + ".text")));
        }
        for (int i = 1; i <= chatMessages.length; i++) {
            chatMessages[i-1] = replaceSymbolsAndNull(config.getString("chat-messages." + i + ".text"));
        }
    }

    void loadFiles() {
        try {
            if (file == null) {
                file = new File(getFolder(), "config.yml");
            }

            if (file.exists()) {
                config = YamlConfiguration.loadConfiguration(file);
                config.load(file);
                reloadConfig();
            } else {
                saveResource("config.yml", false);
                config = YamlConfiguration.loadConfiguration(file);
                logConsole("The 'config.yml' file successfully created!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was a file error.");
        }
    }

    /*private void connectSql() {
        if (config.getBoolean("mysql.use")) {
            String url = "jdbc:mysql://" + config.getString("mysql.host")
                    + ":" + config.getString("mysql.port")
                    + "/" + config.getString("mysql.database");

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                logConsole(Level.WARNING, "jdbc driver unavailable!");
                return;
            }

            try {
                connection = DriverManager.getConnection(url, config.getString("mysql.username"), config.getString("mysql.password"));
                logConsole("Successfully connected to MySql.");
            } catch (SQLException e) {
                e.printStackTrace();
                logConsole(Level.WARNING, "Connection to MySql failed.");
            }
        }
    }

    private void disconnectSql() {
        if (config.getBoolean("mysql.use")) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                logConsole("Successfully disconnected from MySql.");
            } catch (Exception e) {
                e.printStackTrace();
                logConsole(Level.WARNING, "Disconnecting from MySql failed.");
            }
        }
    }*/


                    /*p.sendMessage(rawBlock.toString());
                    String msg = rawBlock.toString();
                    try {
                        try {
                            Class.forName("org.spigotmc.SpigotConfig");

                            BaseComponent[] bc = ComponentSerializer.parse(msg);
                            p.spigot().sendMessage(bc);
                        } catch (ClassNotFoundException e) {
                            String ver = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
                                    .split(",")[3];
                            Object parsedMessage = Class
                                    .forName("net.minecraft.server." + ver + ".IChatBaseComponent$ChatSerializer")
                                    .getMethod("a", new Class[] { String.class }).invoke(null, new Object[] {
                                            org.bukkit.ChatColor.translateAlternateColorCodes("&".charAt(0), msg) });
                            Object packetPlayOutChat = Class.forName("net.minecraft.server." + ver + ".PacketPlayOutChat")
                                    .getConstructor(new Class[] {
                                            Class.forName("net.minecraft.server." + ver + ".IChatBaseComponent") })
                                    .newInstance(new Object[] { parsedMessage });

                            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer")
                                    .cast(p);
                            Object craftHandle = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer")
                                    .getMethod("getHandle", new Class[0]).invoke(craftPlayer, new Object[0]);
                            Object playerConnection = Class.forName("net.minecraft.server." + ver + ".EntityPlayer")
                                    .getField("playerConnection").get(craftHandle);

                            Class.forName("net.minecraft.server." + ver + ".PlayerConnection")
                                    .getMethod("sendPacket",
                                            new Class[] { Class.forName("net.minecraft.server." + ver + ".Packet") })
                                    .invoke(playerConnection, new Object[] { packetPlayOutChat });
                        }
                    } catch (Throwable e) {
                        logConsole(Level.WARNING, "Invalid JSON format: " + msg);
                        return;
                    }*/

    private File getFolder() {
        File folder = getDataFolder();
        if (!folder.exists()) {
            folder.mkdir();
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