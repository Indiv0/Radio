package org.github.indiv0.radio.serialization;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Radio {
    Location location;
    Frequency freq;

    public Radio(final Location location, final Frequency frequency) {
        this.location = location;
        freq = frequency;
    }

    public Block getBlock() {
        return location.getBlock();
    }

    public Location getLocation() {
        return location;
    }

    public Frequency getFrequency() {
        return freq;
    }

    public String getFrequencyAsString() {
        return String.valueOf(freq.freq);
    }
}
