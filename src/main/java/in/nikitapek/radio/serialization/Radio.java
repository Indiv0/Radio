package in.nikitapek.radio.serialization;

import in.nikitapek.radio.util.RadioUtil;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;


public class Radio implements Comparable<Radio> {
    Location location;
    Frequency freq;

    private static final BlockFace[] faces = { BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST };

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

    public static boolean signExists(final Location location, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return location.getBlock().getRelative(face).getType() == Material.WALL_SIGN;
    }

    public static ArrayList<String> getMessage(final Location location) {
        String message = "";
        int val = 0;

        faces: for (val = 0; val < faces.length; val++) {
            if (!signExists(location, faces[val])) {
                continue;
            }

            Sign sign = getSign(location, faces[val]);

            for (int j = 0; j <= 3; j++) {
                if (RadioUtil.hasTags(sign.getLine(j))) {
                    break faces;
                }
            }
        }

        for (int i = 0; i < faces.length; i++) {
            for (int j = 0; j <= 3; j++) {
                if (!signExists(location, faces[(val + i) % 4])) {
                    continue;
                }

                Sign sign = getSign(location, faces[(val + i) % 4]);

                if (!RadioUtil.hasTags(sign.getLine(j))) {
                    String line = sign.getLine(j);
                    message = message.concat(line);
                }
            }
        }

        if (message == "") {
            return new ArrayList<String>();
        }

        // Split the strings and add them to a list.
        String[] strings = message.split("\\\\n");

        for (int i = 1; i < strings.length; i++) {
            strings[i] = strings[i];
        }

        return new ArrayList<String>(Arrays.asList(strings));
    }

    public static Sign getSign(final Location location, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        // Retrieves the sign block at the requested radio face.
        return signExists(location, face) ? (Sign) location.getBlock().getRelative(face).getState() : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((freq == null) ? 0 : freq.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Radio other = (Radio) obj;
        if (freq == null) {
            if (other.freq != null) {
                return false;
            }
        } else if (!freq.equals(other.freq)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Radio o) {
        int c = Integer.compare(location.getBlockX(), o.location.getBlockX());
        if (c != 0) {
            return c;
        }

        c = Integer.compare(location.getBlockY(), o.location.getBlockY());
        if (c != 0) {
            return c;
        }

        return Integer.compare(location.getBlockZ(), o.location.getBlockZ());
    }
}
