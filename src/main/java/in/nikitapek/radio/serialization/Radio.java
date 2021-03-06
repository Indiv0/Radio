package in.nikitapek.radio.serialization;

import in.nikitapek.radio.util.RadioConfigurationContext;
import in.nikitapek.radio.util.RadioUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Radio implements Comparable<Radio> {
    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};

    private final Frequency freq;
    private final Location location;

    public Radio(Location location, Frequency frequency) {
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

    public static boolean signExists(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return location.getBlock().getRelative(face).getType() == Material.WALL_SIGN;
    }

    public static List<String> getMessage(Location location) {
        String message = "";
        int val;

        faces:
        for (val = 0; val < FACES.length; val++) {
            if (!signExists(location, FACES[val])) {
                continue;
            }

            Sign sign = getSign(location, FACES[val]);

            for (int j = 0; j <= 3; j++) {
                if (RadioUtil.hasTags(sign.getLine(j))) {
                    break faces;
                }
            }
        }

        for (int i = 0; i < FACES.length; i++) {
            for (int j = 0; j <= 3; j++) {
                if (!signExists(location, FACES[(val + i) % 4])) {
                    continue;
                }

                Sign sign = getSign(location, FACES[(val + i) % 4]);

                if (!RadioUtil.hasTags(sign.getLine(j))) {
                    String line = sign.getLine(j);
                    message = message.concat(line);
                }
            }
        }

        if ("".equals(message)) {
            return new ArrayList<>();
        }

        // Split the strings based on newline characters and return them.
        return new ArrayList<>(Arrays.asList(message.split("\\\\n")));
    }

    public static Sign getSign(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        // Retrieves the sign block at the requested radio face.
        return signExists(location, face) ? (Sign) location.getBlock().getRelative(face).getState() : null;
    }

    public double getBroadcastClarity(Block block) {
        Double clarityValue = RadioConfigurationContext.signalClarityBlocks.get(block.getType());

        if (clarityValue == null) {
            return 0.1;
        }

        return clarityValue;
    }

    public boolean isClarityBlock(Block block) {
        return RadioConfigurationContext.signalClarityBlocks.containsKey(block.getType());
    }

    public static ChatColor getChatColor(Block block) {
        switch (((Wool) block.getState().getData()).getColor()) {
            case BLACK:
                return ChatColor.BLACK;
            case BLUE:
                return ChatColor.BLUE;
            case LIGHT_BLUE:
                return ChatColor.AQUA;
            case GRAY:
                return ChatColor.GRAY;
            case GREEN:
                return ChatColor.GREEN;
            case MAGENTA:
                return ChatColor.LIGHT_PURPLE;
            case PURPLE:
                return ChatColor.DARK_PURPLE;
            case RED:
                return ChatColor.RED;
            case WHITE:
                return ChatColor.WHITE;
            case YELLOW:
                return ChatColor.YELLOW;
            default:
                return ChatColor.MAGIC;
        }
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
