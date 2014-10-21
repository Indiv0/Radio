package in.nikitapek.radio.util;

import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class RadioUtil {
    private RadioUtil() {
    }

    public static boolean signHasValidFrequency(Location location, BlockFace face) {
        Sign sign = Radio.getSign(location, face);

        // Checks to make sure there is a sign on that face of the radio.
        if (sign == null) {
            return false;
        }

        return hasTags(sign.getLine(0));
    }

    // Tag related methods

    public static boolean hasTags(String frequency) {
        if (frequency.length() < 3) {
            return false;
        }

        // Checks to make sure the frequency has the proper tags.
        return "[".equals(frequency.substring(0, 1))
                && "]".equals(frequency.substring(frequency.length() - 1));
    }

    public static String stripTags(String frequency) {
        // Returns the frequency without the marker tags.
        return frequency.substring(1, frequency.length() - 1);
    }
}
