package org.github.indiv0.radio.util;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.github.indiv0.radio.main.RadioBroadcast;

public final class RadioUtil {
    private RadioUtil() {
    }

    public static boolean registerFrequencyToSign(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(location, face)) return false;

        // Gets the sign itself.
        Sign sign = getSign(location, face);

        // Checks to make sure there is a sign on that face of the radio.
        if (sign == null) return false;

        BigDecimal signFreq = parseSignStringToFrequency(sign.getLine(0));
        BigDecimal locationFreq = getFrequencyFromLocation(location);
        String radioFreq = RadioBroadcast.plugin.getRadios().get(location);

        // If the sign frequency does not contain a valid value, sets it
        // to the radio frequency.
        sign.setLine(0, radioFreq);

        // If there is a defined frequency for this radio, uses it.
        if (signFreq != null)
            // If the sign frequency contains a valid value, and if the
            // radio frequency is based on the location, sets the radio
            // frequency to it.
            if (radioFreq.equals(locationFreq)) RadioBroadcast.plugin.addRadio(location, signFreq.toString());

        sign.update(true);

        return true;
    }

    public static boolean isStringValidFrequency(String stringFrequency) {
        if (parseStringToFrequency(stringFrequency) == null)
            return false;

        return true;
    }

    public static boolean isSignStringValidFrequency(String stringFrequency) {
        if (parseSignStringToFrequency(stringFrequency) == null)
            return false;

        return true;
    }

    public static BigDecimal parseSignStringToFrequency(String stringFrequency) {
        if (stringFrequency.substring(0, 0) != "[" ||
                stringFrequency.substring(stringFrequency.length()) != "]")
            return null;

        return parseStringToFrequency(stringFrequency);
    }

    public static BigDecimal parseStringToFrequency(String stringFrequency) {
        String isolatedString = String.copyValueOf(stringFrequency.toCharArray(), 1, stringFrequency.length() - 2);

        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.parseDouble(isolatedString));
        } catch (NumberFormatException e) {
            return null;
        }

        return frequency;
    }

    private static boolean isSignExistant(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return (getBlock(location).getRelative(face).getType() == Material.WALL_SIGN);
    }

    // Getter and Setter Methods
    public static BigDecimal getFrequencyFromLocation(Location location) {
        // Reverts the frequency from scientific to integer notation.
        String frequency = String.valueOf(location.hashCode());

        return parseSignStringToFrequency(frequency);
    }

    public static String getMessage(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(location, face)) return null;

        // Formulates a message based on the text on the sign.
        return getSign(location, face).getLine(1) + " " + getSign(location, face).getLine(2) + " " + getSign(location, face).getLine(3);
    }

    private static Sign getSign(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(location, face)) return null;

        // Retrieves the sign block at the requested radio face.
        return (Sign) getBlock(location).getRelative(face).getState();
    }

    private static Block getBlock(Location location) {
        // Gets the block at the location provided.
        return location.getBlock();
    }
}
