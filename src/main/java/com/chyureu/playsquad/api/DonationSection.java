package com.chyureu.playsquad.api;

import java.util.List;

public class DonationSection {
    private final String key;
    private final List<DonationCommand> commands;

    public DonationSection(String key, List<DonationCommand> commands) {
        this.key = key;
        this.commands = commands;
    }

    public String getKey() {
        return key;
    }

    public List<DonationCommand> getCommands() {
        return commands;
    }
}
