package org.github.indiv0.radio.util;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;

public final class RadioUtil {
    public static boolean registerFrequencyToSign(Radio radio, final BlockFace face) {
        // Gets the sign itself.
        final Sign sign = Radio.getSign(radio.getLocation(), face);

        if (!signHasValidFrequency(radio.getLocation(), face))
            return false;

        final BigDecimal signFreq = getFrequencyFromStringWithoutTags(sign.getLine(0));
        final Frequency radioFreq = radio.getFrequency();

        // If the sign frequency does not contain a valid value, sets it
        // to the radio frequency.
        sign.setLine(0, addTags(radioFreq));

        // If there is a defined frequency for this radio, uses it.
        if (signFreq != null)
            // If the sign frequency contains a valid value, and if the
            // radio frequency is based on the location, sets the radio
            // frequency to it.
            if (radioFreq == null) {
                radio.getFrequency().setFrequency(signFreq);
                sign.setLine(0, addTags(signFreq));
            }

        sign.update(true);

        return true;
    }

    public static boolean signHasValidFrequency(final Location location, final BlockFace face) {
        // Confirms that the requested side of the radio has a sign.
        if (!Radio.signExists(location, face))
            return false;

        // Checks to make sure there is a sign on that face of the radio.
        if (Radio.getSign(location, face) == null)
            return false;

        if (getFrequencyFromStringWithoutTags(Radio.getSign(location, face).getLine(0)) == null)
            return false;

        return true;
    }

    // Tag related methods

    public static boolean hasTags(String frequency) {
        if (frequency.length() < 3)
            return false;

        // Checks to make sure the frequency has the proper tags.
        return frequency.substring(0, 1).equals("[")
                && frequency.substring(frequency.length() - 1).equals("]");
    }

    private static String stripTags(String frequency) {
        // Returns the frequency without the marker tags.
        return frequency.substring(1, frequency.length() - 1);
    }

    private static String addTags(Object frequency) {
        return "[" + frequency + "]";
    }

    public static boolean playerIsHoldingPipboy(final Player player) {
        // Makes sure that the currently held item is the "Pipboy" (by default
        // the compass).
        if (player.getItemInHand().getTypeId() != RadioBroadcast.plugin.getPipboyID()) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return false;
        }

        return true;
    }

    // Getter and Setter Methods

    public static BigDecimal getFrequencyFromString(final String stringFrequency) {
        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.parseDouble(stringFrequency));
        } catch (final NumberFormatException e) {
            return null;
        }

        return frequency;
    }

    public static BigDecimal getFrequencyFromStringWithoutTags(final String stringFrequency) {
        // Returns the frequency without the tags attached.
        return hasTags(stringFrequency) ? getFrequencyFromString(stripTags(stringFrequency))
                : null;
    }
}
