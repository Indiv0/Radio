package org.github.indiv0.serialization;

import org.bukkit.Location;

public class Radio {
    Location location;
    Frequency freq;

    public Radio(Location location, Frequency frequency) {
        this.location = location;
        freq = frequency;
    }
}
