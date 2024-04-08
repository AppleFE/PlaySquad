package com.chyureu.playsquad.api;

public class DonationCommand {
    private final String command;
    private final double probability;

    public DonationCommand(String command, double probability) {
        this.command = command;
        this.probability = probability;
    }

    public String getCommand() {
        return command;
    }

    public double getProbability() {
        return probability;
    }
}
