package in.nikitapek.radio.util;

import in.nikitapek.radio.serialization.Radio;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public final class RadioUtil {
    private RadioUtil() {}

    public static boolean signHasValidFrequency(final Location location, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!Radio.signExists(location, face)) {
            return false;
        }

        // Checks to make sure there is a sign on that face of the radio.
        if (Radio.getSign(location, face) == null) {
            return false;
        }

        final BigDecimal frequency = getFrequencyFromStringWithoutTags(Radio.getSign(location, face).getLine(0));

        if (frequency == null) {
            return false;
        }

        return BigDecimal.ZERO.compareTo(frequency) < 0;
    }

    // Tag related methods

    public static boolean hasTags(final String frequency) {
        if (frequency.length() < 3) {
            return false;
        }

        // Checks to make sure the frequency has the proper tags.
        return "[".equals(frequency.substring(0, 1))
                && "]".equals(frequency.substring(frequency.length() - 1));
    }

    private static String stripTags(final String frequency) {
        // Returns the frequency without the marker tags.
        return frequency.substring(1, frequency.length() - 1);
    }

    // Getter and Setter Methods

    public static BigDecimal getFrequencyFromString(final String stringFrequency) {
        try {
            return new BigDecimal(stringFrequency);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal getFrequencyFromStringWithoutTags(final String stringFrequency) {
        // Returns the frequency without the tags attached.
        return hasTags(stringFrequency) ? getFrequencyFromString(stripTags(stringFrequency)) : null;
    }
}
