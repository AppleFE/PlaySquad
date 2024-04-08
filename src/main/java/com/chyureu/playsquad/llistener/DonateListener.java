package com.chyureu.playsquad.llistener;

import com.chyureu.playsquad.PlaySquad;
import com.chyureu.playsquad.api.DonationCommand;
import com.chyureu.playsquad.config.SettingManager;
import com.chyureu.playsquad.events.PlaySquadDonationEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DonateListener implements Listener {
    private SettingManager settingManager;

    // Event handler for PlaySquadDonationEvent
    @EventHandler
    public void onDonate(PlaySquadDonationEvent event) {
        System.out.println("Amount: " + event.getAmount());
        System.out.println("EventID: " + event.getEventID());
        System.out.println("GuestName: " + event.getGuestName());
        System.out.println("SquadName: " + event.getSquadName());
        System.out.println("clientMessage: " + event.getClientMessage());
        settingManager = SettingManager.getInstance(PlaySquad.getPlugin());

        // Get a random command based on the donation amount
        String command = getRandomCommand(event.getAmount());

        // Replace variables in the command string
        command = replaceVariables(command);

        // Execute the command as console
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        Bukkit.broadcastMessage("Command: " + command);
    }

    // Replace placeholders in the command string with actual values
    private String replaceVariables(String command) {
        // Replace {player} with the streamer's name
        command = command.replaceAll("\\{player}", settingManager.getStreamer());

        // Replace {random_player} with a random online player's name (excluding whitelist)
        Player randomPlayer = getRandomPlayerExcludingWhitelist();
        if (randomPlayer != null) {
            command = command.replaceAll("\\{random_player}", randomPlayer.getName());
        } else {
            command = command.replaceAll("\\{random_player}", "");
        }

        // Replace {all_players} with a comma-separated list of online players' names (excluding whitelist)
        command = command.replaceAll("\\{all_players}", getAllOnlinePlayers());

        return command;
    }

    // Get a random online player excluding players in the whitelist
    private Player getRandomPlayerExcludingWhitelist() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.removeIf(player -> {
            for (String whitelistName : settingManager.getWhitelist()) {
                if (player.getName().startsWith(whitelistName)) {
                    return true;
                }
            }
            return false;
        });

        if (!onlinePlayers.isEmpty()) {
            Random random = new Random();
            return onlinePlayers.get(random.nextInt(onlinePlayers.size()));
        }

        return null;
    }


    // Get a comma-separated string of online players' names excluding players in the whitelist
    private String getAllOnlinePlayers() {
        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!settingManager.getWhitelist().contains(player.getName())) {
                playerNames.add(player.getName());
            }
        }
        return String.join(",", playerNames);
    }

    // Get a random command based on the donation amount's key and its associated probabilities
    private String getRandomCommand(String key) {
        List<DonationCommand> commands = settingManager.getDonationValue(key);
        System.out.println(commands);

        Random random = new Random();

        double cumulativeProbability = 0.0;

        double randomValue = random.nextDouble();

        for (DonationCommand command : commands) {
            cumulativeProbability += command.getProbability();

            if (cumulativeProbability >= randomValue) {
                return command.getCommand();
            }
        }

        return null;
    }
}