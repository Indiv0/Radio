package org.github.indiv0.radio.util;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;

public final class RadioUtil {
    private RadioUtil() {
    }

    public static boolean registerFrequencyToSign(Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!RadioUtil.isSignExistant(radio, face))
            return false;

        // Gets the sign itself.
        final Sign sign = RadioUtil.getSign(radio, face);

        // Checks to make sure there is a sign on that face of the radio.
        if (sign == null)
            return false;

        final BigDecimal signFreq = RadioUtil.parseSignStringToFrequency(sign.getLine(0));
        final BigDecimal locationFreq = RadioUtil.getFrequencyFromLocation(radio.getLocation());
        final String radioFreq = radio.getFrequencyAsString();

        // If the sign frequency does not contain a valid value, sets it
        // to the radio frequency.
        sign.setLine(0, radioFreq);

        // If there is a defined frequency for this radio, uses it.
        if (signFreq != null)
            // If the sign frequency contains a valid value, and if the
            // radio frequency is based on the location, sets the radio
            // frequency to it.
            if (radioFreq.equals(locationFreq)) {
                radio = new Radio(radio.getLocation(), new Frequency(signFreq));
            }

        sign.update(true);

        return true;
    }

    public static boolean isStringValidFrequency(final String stringFrequency) {
        if (RadioUtil.parseStringToFrequency(stringFrequency) == null)
            return false;

        return true;
    }

    public static boolean isSignStringValidFrequency(final String stringFrequency) {
        if (RadioUtil.parseSignStringToFrequency(stringFrequency) == null)
            return false;

        return true;
    }

    public static BigDecimal parseSignStringToFrequency(final String stringFrequency) {
        if (stringFrequency.substring(0, 0) != "["
                || stringFrequency.substring(stringFrequency.length()) != "]")
            return null;

        return RadioUtil.parseStringToFrequency(stringFrequency);
    }

    public static BigDecimal parseStringToFrequency(final String stringFrequency) {
        final String isolatedString = String.copyValueOf(stringFrequency.toCharArray(), 1, stringFrequency.length() - 2);

        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.parseDouble(isolatedString));
        } catch (final NumberFormatException e) {
            return null;
        }

        return frequency;
    }

    private static boolean isSignExistant(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return radio.getBlock().getRelative(face).getType() == Material.WALL_SIGN;
    }

    // Getter and Setter Methods
    public static BigDecimal getFrequencyFromLocation(final Location location) {
        // Reverts the frequency from scientific to integer notation.
        final String frequency = String.valueOf(location.hashCode());

        return RadioUtil.parseSignStringToFrequency(frequency);
    }

    public static String getMessage(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!RadioUtil.isSignExistant(radio, face))
            return null;

        // Formulates a message based on the text on the sign.
        return RadioUtil.getSign(radio, face).getLine(1) + " "
                + RadioUtil.getSign(radio, face).getLine(2) + " "
                + RadioUtil.getSign(radio, face).getLine(3);
    }

    private static Sign getSign(final Radio radio, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!RadioUtil.isSignExistant(radio, face))
            return null;

        // Retrieves the sign block at the requested radio face.
        return (Sign) radio.getBlock().getRelative(face).getState();
    }
}
