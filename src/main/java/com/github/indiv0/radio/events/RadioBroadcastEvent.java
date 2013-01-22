package com.github.indiv0.radio.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RadioBroadcastEvent extends Event {

    public RadioBroadcastEvent(Location source, String message, ChatColor color, int distance, double clarity) {

    }

    @Override
    public HandlerList getHandlers() {
        // TODO Auto-generated method stub
        return null;
    }
}
