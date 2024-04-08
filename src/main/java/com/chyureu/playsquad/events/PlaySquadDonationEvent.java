package com.chyureu.playsquad.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlaySquadDonationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String amount;
    private final String eventID;
    private final String guestName;
    private final String squadName;
    private final String clientMessage;
    public PlaySquadDonationEvent(String amount, String eventID, String guestName, String squadName, String clientMessage) {
        this.amount = amount;
        this.eventID = eventID;
        this.guestName = guestName;
        this.squadName = squadName;
        this.clientMessage = clientMessage;
    }

    public String getAmount() {
        return amount;
    }

    public String getEventID() {
        return eventID;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getSquadName() {
        return squadName;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
