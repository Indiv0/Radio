package org.github.indiv0.radio.blah;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class Radio {
    public static boolean registerFrequencyToSign(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!isSignExistant(location, face)) return false;

        // Gets the sign itself.
        Sign sign = getSign(location, face);

        // Checks to make sure there is a sign on that face of the radio.
        if (sign == null) return false;

        String signFreq = convertFrequencyToIntegerNotation(sign.getLine(0));
        String locationFreq = getFrequencyFromLocation(location);
        String radioFreq = RadioBroadcast.plugin.getRadios().get(location);

        // If the sign frequency does not contain a valid value, sets it
        // to the radio frequency.
        sign.setLine(0, radioFreq);

        // If there is a defined frequency for this radio, uses it.
        if (signFreq != null && signFreq != "")
            // If the sign frequency contains a valid value, and if the
            // radio frequency is based on the location, sets the radio
            // frequency to it.
            if (radioFreq.equals(locationFreq)) RadioBroadcast.plugin.addRadio(location, signFreq);

        sign.update(true);

        return true;
    }

    public static String convertFrequencyToIntegerNotation(String frequency) {
        // Reverts the frequency from scientific to integer notation.
        double doubleFrequency;
        try {
            doubleFrequency = Double.parseDouble(frequency);
        } catch (NumberFormatException e) {
            return null;
        }

        NumberFormat formatter = new DecimalFormat("#########");

        return formatter.format(doubleFrequency);
    }

    private static boolean isSignExistant(Location location, BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        return (getBlock(location).getRelative(face).getType() == Material.WALL_SIGN);
    }

    // Getter and Setter Methods
    public static String getFrequencyFromLocation(Location location) {
        // Reverts the frequency from scientific to integer notation.
        String frequency = String.valueOf(location.hashCode());

        return convertFrequencyToIntegerNotation(frequency);
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
