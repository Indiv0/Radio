package org.github.indiv0.radio.util;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.github.indiv0.radio.serialization.Radio;

public final class RadioUtil {
    private RadioUtil() {
    }

    public static boolean registerFrequencyToSign(Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(radio, face))
            return false;

        // Gets the sign itself.
        final Sign sign = getSign(radio, face);

        // Checks to make sure there is a sign on that face of the radio.
        if (sign == null)
            return false;

        final BigDecimal signFreq = parseSignStringToFrequency(sign.getLine(0));
        final BigDecimal locationFreq = getFrequencyFromLocation(radio.getLocation());
        final BigDecimal radioFreq = radio.getFrequency().getFrequency();

        // If the sign frequency does not contain a valid value, sets it
        // to the radio frequency.
        sign.setLine(0, "[" + radioFreq + "]");

        // If there is a defined frequency for this radio, uses it.
        if (signFreq != null)
            // If the sign frequency contains a valid value, and if the
            // radio frequency is based on the location, sets the radio
            // frequency to it.
            if (radioFreq.equals(locationFreq)) {
                radio.getFrequency().setFrequency(signFreq);
                sign.setLine(0, "[" + signFreq + "]");
            }

        sign.update(true);

        return true;
    }

    public static BigDecimal parseSignStringToFrequency(final String stringFrequency) {
        // Returns the frequency without the tags attached.
        return hasTags(stringFrequency) ? null
                : parseStringToFrequency(stripTags(stringFrequency));
    }

    public static BigDecimal parseStringToFrequency(final String stringFrequency) {
        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.parseDouble(stringFrequency));
        } catch (final NumberFormatException e) {
            return null;
        }

        return frequency;
    }

    // Getter and Setter Methods
    public static BigDecimal getFrequencyFromLocation(final Location location) {
        // Reverts the frequency from scientific to integer notation.
        final String frequency = String.valueOf(location.hashCode());

        return parseStringToFrequency(frequency);
    }

    private static boolean isSignExistant(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return radio.getBlock().getRelative(face).getType() == Material.WALL_SIGN;
    }

    public static String getMessage(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(radio, face))
            return null;

        Sign sign = getSign(radio, face);

        // Formulates a message based on the text on the sign.
        return sign.getLine(1) + " " + sign.getLine(2) + " " + sign.getLine(3);
    }

    private static Sign getSign(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(radio, face))
            return null;

        // Retrieves the sign block at the requested radio face.
        return (Sign) radio.getBlock().getRelative(face).getState();
    }

    private static boolean hasTags(String frequency) {
        // Checks to make sure the frequency has the proper tags.
        return frequency.substring(0, 1).equals("[")
                && frequency.substring(frequency.length() - 1).equals("]");
    }

    private static String stripTags(String frequency) {
        // Returns the frequency without the marker tags.
        return frequency.substring(1, frequency.length() - 1);
    }
}
