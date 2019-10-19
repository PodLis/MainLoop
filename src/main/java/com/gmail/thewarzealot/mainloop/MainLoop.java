package com.gmail.thewarzealot.mainloop;

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

    private char bar_counter;
    private char loop_counter;

    private FileConfiguration config;
    private File file;
    private BukkitScheduler scheduler;
    //private Connection connection;

    private BossBar[] bars;

    @Override
    public void onEnable() {

        loadFiles();

        //connectSql();

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

        //disconnectSql();

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
        int bar_mark = config.getInt("boss-bars.delay");
        int feed_mark = config.getInt("periodic-feed.delay");

        String console_command = config.getString("periodic-feed.cmd-to-console");
        String player_command = config.getString("periodic-feed.cmd-to-player");

        determineBars();
        bar_counter = Character.MAX_VALUE;

        scheduler.scheduleSyncRepeatingTask(this, () -> {

            if (loop_counter % bar_mark == 0) {
                bars[bar_counter % bars.length].removeAll();
                bar_counter++;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    bars[bar_counter % bars.length].addPlayer(p);
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
        for (BossBar bar : bars) {
            bar.removeAll();
        }
        scheduler.cancelTasks(this);
    }

    private void determineBars() {
        bars = new BossBar[config.getInt("boss-bars.number-of")];
        for (int i = 1; i <= bars.length; i++) {
            bars[i-1] = getServer().createBossBar(
                    replaceColor(config.getString("boss-bars." + i + ".text")),
                    BarColor.valueOf(config.getString("boss-bars." + i + ".color")),
                    BarStyle.valueOf(config.getString("boss-bars." + i + ".segmented")));
            bars[i-1].setProgress(config.getDouble("boss-bars." + i + ".progress"));
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

    private String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }
}