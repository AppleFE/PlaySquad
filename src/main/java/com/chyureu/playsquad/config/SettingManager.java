package com.chyureu.playsquad.config;

import com.chyureu.playsquad.api.DonationCommand;
import com.chyureu.playsquad.api.DonationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingManager {

    private static SettingManager instance;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;
    private String tokenKey;
    private String streamerKey;
    private String whitelistKey;
    private String donationSettingsKey;
    private String token;
    private String streamer;
    private List<String> whitelist;
    private List<DonationSection> donationSettings;

    private SettingManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "settings.yml");
        if (!configFile.exists()) {
            plugin.saveResource("settings.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadValues();
    }

    public static SettingManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new SettingManager(plugin);
        }
        return instance;
    }

    private void loadValues() {
        this.tokenKey = "token";
        this.streamerKey = "streamer";
        this.whitelistKey = "whitelist";
        this.donationSettingsKey = "donation_settings";
        this.token = config.getString(tokenKey);
        this.streamer = config.getString(streamerKey);
        this.whitelist = config.getStringList(whitelistKey);
        this.donationSettings = parseDonationSettings();
    }

    private List<DonationSection> parseDonationSettings() {
        List<DonationSection> donationSections = new ArrayList<>();
        if (config.contains(donationSettingsKey)) {
            for (String key : config.getConfigurationSection(donationSettingsKey).getKeys(false)) {
                List<DonationCommand> commands = new ArrayList<>();
                for (Object obj : config.getList(donationSettingsKey + "." + key)) {
                    if (obj instanceof String) {
                        // If the entry is a string, assume it's a command without probability
                        commands.add(new DonationCommand((String) obj, 1.0));
                    } else if (obj instanceof Map) {
                        // If the entry is a map, assume it's a command with probability
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String command = (String) map.get("command");
                        double probability = (Double) map.get("probability");
                        commands.add(new DonationCommand(command, probability));
                    }
                }
                donationSections.add(new DonationSection(key, commands));
            }
        }
        return donationSections;
    }

    // Getter for token
    public String getToken() {
        return token;
    }

    // Getter for streamer
    public String getStreamer() {
        return streamer;
    }

    // Getter for whitelist
    public List<String> getWhitelist() {
        return whitelist;
    }

    // Getter for donation settings
    public List<DonationSection> getDonationSettings() {
        return donationSettings;
    }

    // Setter for streamer
    public void setStreamer(String streamer) {
        this.streamer = streamer;
        config.set(streamerKey, streamer);
        saveConfig();
    }

    // Setter for whitelist
    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
        config.set(whitelistKey, whitelist);
        saveConfig();
    }

    // Getter for donation settings keys
    public List<String> getDonationKeyList() {
        return new ArrayList<>(config.getConfigurationSection(donationSettingsKey).getKeys(false));
    }

    // Getter for donation settings values as a list of DonationCommand
    public List<DonationCommand> getDonationValueList(String key) {
        List<DonationCommand> commands = new ArrayList<>();
        for (Object obj : config.getList(donationSettingsKey + "." + key)) {
            if (obj instanceof String) {
                commands.add(new DonationCommand((String) obj, 1.0));
            } else if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) obj;
                String command = (String) list.get(0);
                double probability = (double) list.get(1);
                commands.add(new DonationCommand(command, probability));
            }
        }
        return commands;
    }

    // Getter for donation settings values as a list of DonationCommand with probability
    public List<DonationCommand> getDonationValue(String key) {
        List<DonationCommand> commands = new ArrayList<>();
        for (DonationSection section : donationSettings) {
            String sectionKey = section.getKey();
            if (sectionKey.equals(key)) {
                return section.getCommands();
            } else {
                if (sectionKey.contains("~")) {
                    String[] range = sectionKey.split("~");
                    int lowerBound = Integer.parseInt(range[0]);
                    int upperBound = Integer.parseInt(range[1]);
                    int keyValue = Integer.parseInt(key);
                    if (keyValue >= lowerBound && keyValue <= upperBound) {
                        return section.getCommands();
                    }
                }
            }
        }
        return commands;
    }

    // Save configuration to file
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reload configuration
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadValues();
    }
}
