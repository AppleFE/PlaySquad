package com.chyureu.playsquad;

import com.chyureu.playsquad.commands.ReloadCommand;
import com.chyureu.playsquad.config.SettingManager;
import com.chyureu.playsquad.listener.DonateListener;
import com.chyureu.playsquad.websocket.WebSocketEventClient;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaySquad extends JavaPlugin {

    private SettingManager settingManager;
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        settingManager = SettingManager.getInstance(this);
        WebSocketEventClient webSocketClient = new WebSocketEventClient();
        getServer().getPluginManager().registerEvents(new DonateListener(), this);
        getCommand("리로드").setExecutor(new ReloadCommand(this));
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
