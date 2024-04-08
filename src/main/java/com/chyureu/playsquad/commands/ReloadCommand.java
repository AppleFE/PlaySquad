package com.chyureu.playsquad.commands;

import com.chyureu.playsquad.api.DonationCommand;
import com.chyureu.playsquad.config.SettingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage("§c관리자 전용 명령어 입니다.");
            return false;
        }
        Player player = (Player) sender;
        SettingManager manager = SettingManager.getInstance(plugin);
        manager.reload();
        player.sendMessage("§asettings.yml 파일을 리로드 했습니다.");
        String streamer = manager.getStreamer();
        List<String> whitelist = manager.getWhitelist();
        player.sendMessage("§bStreamer§f: §b" + streamer);
        player.sendMessage("§eWhitelist§f: §e" + whitelist.toString());
        for (String key : manager.getDonationKeyList()) {
            player.sendMessage("==========================================");
            player.sendMessage("Key: " + key);
            for (DonationCommand donationCommand : manager.getDonationValue(key)) {
                player.sendMessage("§aCommand§f: §e" + donationCommand.getCommand() + " §f/ §cProbability§f: §e" + donationCommand.getProbability());
            }
        }
        return false;
    }
}
