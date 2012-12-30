package org.github.indiv0.radio.serialization;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class Radio implements Comparable<Radio> {
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

    public boolean signExists(final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return getBlock().getRelative(face).getType() == Material.WALL_SIGN;
    }

    public String getMessage(final BlockFace face) {
        Sign sign = getSign(face);

        // Confirms that the requested side of the radio has a sign.
        // Formulates a message based on the text on the sign.
        return signExists(face) ? sign.getLine(1) + " " + sign.getLine(2) + " "
                + sign.getLine(3) : null;
    }

    public Sign getSign(final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        // Retrieves the sign block at the requested radio face.
        return signExists(face) ? (Sign) getBlock().getRelative(face).getState()
                : null;
    }

    // Comparable methods
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (freq == null ? 0 : freq.hashCode());
        result = prime * result + (location == null ? 0 : location.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Radio))
            return false;
        final Radio other = (Radio) obj;
        if (freq == null) {
            if (other.freq != null)
                return false;
        } else if (!freq.equals(other.freq))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }

    @Override
    public int compareTo(final Radio obj) {
        final int c = freq.compareTo(obj.freq);

        if (c == 0) {
            if (location.getX() == obj.location.getX())
                if (location.getY() == obj.location.getY())
                    if (location.getZ() == obj.location.getZ())
                        if (location.getWorld().getEnvironment().compareTo(obj.location.getWorld().getEnvironment()) == 0)
                            return c;
        }

        return -1;
    }
}
