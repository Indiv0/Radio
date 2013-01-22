package com.github.indiv0.radio.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import ashulman.typesafety.TypeSafeSet;

import com.github.indiv0.radio.management.RadioInfoManager;
import com.github.indiv0.radio.serialization.Frequency;
import com.github.indiv0.radio.serialization.Radio;
import com.github.indiv0.radio.util.RadioConfigurationContext;

public class RadioBroadcast implements Runnable {
    private final RadioInfoManager infoManager;

    private final int pipboyId;
    private final int ironBarExtension;
    private final boolean transmitEmptyMessages;
    private final double scanChance;

    public RadioBroadcast(RadioConfigurationContext configurationContext) {
        infoManager = configurationContext.infoManager;
        pipboyId = configurationContext.pipboyId;
        ironBarExtension = configurationContext.ironBarExtension;
        transmitEmptyMessages = configurationContext.transmitEmptyMessages;
        scanChance = configurationContext.scanChance;
    }

    @Override
    public void run() {
        TypeSafeSet<Radio> radios = infoManager.getRadios();

        // Retrieves all of the available radios, making sure that the list is
        // not empty.
        if (radios == null || radios.isEmpty()) {
            return;
        }

        // Iterates through every radio in the list.
        for (final Radio radio : radios) {
            attemptBroadcast(radio);
        }
    }

    public void attemptBroadcast(final Radio radio) {
        // Retrieves the block which represents the radio.
        Block radioBlock = radio.getBlock();

        // Moves on to the next radio if the current one is not powered.
        if (!radioBlock.isBlockIndirectlyPowered())
            return;

        // Checks to make sure the radio has signs on it.
        for (BlockFace face : BlockFace.values()) {
            if (Material.WALL_SIGN.equals(radioBlock.getRelative(face).getType())) {
                break;
            }
        }

        // Checks to see if the radio has a clarity enchancing block (e.g. Iron,
        // Gold, Lapus, or Diamond).
        Material baseMaterial = radioBlock.getRelative(0, 1, 0).getType();
        if (Material.IRON_BLOCK.equals(baseMaterial)
                || Material.GOLD_BLOCK.equals(baseMaterial)
                || Material.LAPIS_BLOCK.equals(baseMaterial)
                || Material.DIAMOND_BLOCK.equals(baseMaterial)) {
            radioBlock = radioBlock.getRelative(0, 1, 0);
        } else {
            baseMaterial = Material.DIRT;
        }

        // Gets the broadcastGarble, which represents the clarity of the signal,
        // as affected by the clarity enchanting block.
        double broadcastClarity;
        switch (baseMaterial) {
            case IRON_BLOCK:
                broadcastClarity = 0.15;
                break;
            case LAPIS_BLOCK:
                broadcastClarity = 0.3;
                break;
            case GOLD_BLOCK:
                broadcastClarity = 0.5;
                break;
            case DIAMOND_BLOCK:
                broadcastClarity = 0.99;
                break;
            default:
                broadcastClarity = 0;
                break;
        }

        ChatColor color = ChatColor.GOLD;
        Block relativeBlock = radioBlock.getRelative(0, 1, 0);
        if (relativeBlock.getType().equals(Material.WOOL)) {
            DyeColor dyeColor = ((Wool) relativeBlock.getState().getData()).getColor();
            switch (dyeColor) {
                case BLACK:
                    color = ChatColor.BLACK;
                    break;
                case BLUE:
                    color = ChatColor.BLUE;
                    break;
                case LIGHT_BLUE:
                    color = ChatColor.AQUA;
                    break;
                case GRAY:
                    color = ChatColor.GRAY;
                    break;
                case GREEN:
                    color = ChatColor.GREEN;
                    break;
                case MAGENTA:
                    color = ChatColor.LIGHT_PURPLE;
                    break;
                case PURPLE:
                    color = ChatColor.DARK_PURPLE;
                    break;
                case RED:
                    color = ChatColor.RED;
                    break;
                case WHITE:
                    color = ChatColor.WHITE;
                    break;
                case YELLOW:
                    color = ChatColor.YELLOW;
                    break;
                default:
                    color = ChatColor.MAGIC;
                    break;
            }

            radioBlock = relativeBlock;
        }

        // Checks how many IRON_FENCE blocks there are above the structure,
        // capping the number at 16. IRON_FENCE blocks determine the range of
        // the
        // signal.
        int ironBarCount;
        for (ironBarCount = 0; Material.IRON_FENCE.equals(radioBlock.getRelative(0, 1, 0).getType())
                && ironBarCount <= 16; ironBarCount++) {
            radioBlock = radioBlock.getRelative(0, 1, 0);
        }

        // Determines the broadcastDistance, which is a factor of the garble and
        // the IRON_FENCE count.
        final double broadcastDistance = ironBarCount * ironBarExtension
                + broadcastClarity * 550;

        // Retrieves the list of players who will act as recipients of the
        // message, and stores them in an array.
        final List<Player> recipients = radioBlock.getWorld().getPlayers();
        final Player[] recipientsArray = recipients.toArray(new Player[recipients.size()]);

        for (final Player player : recipientsArray) {
            // Checks to make sure that the player and radio are in the same
            // world.
            if (player.getLocation().getWorld() != radio.getLocation().getWorld()) {
                break;
            }

            // Gets the distance of the player from the radio.
            double distance = player.getLocation().distance(radio.getLocation());

            // Alters the distance based on the current weather conditions in
            // the world.
            if (player.getWorld().hasStorm()) {
                distance = 2 * distance;
            }
            if (player.getWorld().isThundering()) {
                distance = 0;
            }

            // Gets the number of IRON_FENCE blocks surrounding the player, and
            // increases the strength of the signal accordingly.
            ironBarCount = 0;
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 1, 0, 0);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 0, 0, 1);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, -1, 0, 0);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 0, 0, -1);
            distance -= ironBarCount * 30;

            // Fails to broadcast if the player is beyond the broadcastDistance.
            if (distance > broadcastDistance) {
                break;
            }

            final double garbleDistance = broadcastClarity * broadcastDistance;
            final double percent = (distance - garbleDistance)
                    / (broadcastDistance - garbleDistance);

            final boolean isGarbled = distance > garbleDistance;

            // Attempts to broadcast the messages contained on the signs on each
            // side of the block.
            broadcast(radio, percent, isGarbled, player, color);
        }
    }

    public void broadcast(final Radio radio, final double percent, final boolean isGarbled, final Player player, ChatColor color) {
        // Fails to broadcast if the frequency of the player's radio and the
        // radio that is broadcasting don't match up.
        if (!checkIfPlayerFrequencyMatches(player, radio))
            return;

        String freqPrefix = ChatColor.RED + "[Radio "
                + radio.getFrequency().getFrequency() + "] " + color;

        ArrayList<String> message = Radio.getMessage(radio.getLocation());

        // Cancels the broadcast if there is no message provided.
        if (message.size() == 0) {
            if (transmitEmptyMessages) {
                player.sendMessage(freqPrefix);
                return;
            } else
                return;
        }

        // Garbles the message if the player is past the garbleDistance;
        if (isGarbled)
            garbleMessage(message, percent);

        message.add(0, freqPrefix + message.remove(0));

        player.sendMessage(message.toArray(new String[message.size()]));
    }

    public static void garbleMessage(final List<String> message, final double percent) {
        // Garbles the message.
        final int messageLength = message.size();
        final int amountRemoved = (int) (percent * messageLength);

        for (String string : message) {
            final char[] charString = string.toCharArray();
            for (int k = 0; k < amountRemoved; k++) {
                final int removalPoint = (int) (Math.random() * (charString.length - 1));
                charString[removalPoint] = ' ';
            }
        }
    }

    public static int calculateIronBarsSurroundingPlayer(final Player player, final int modX, final int modY, final int modZ) {
        // Gets the block at the requested offset.
        Block currentBlock = player.getLocation().getBlock().getRelative(modX, modY, modZ);

        // Checks to make sure that the block has IRON_FENCE blocks.
        if (player.getLocation().getBlock().getRelative(modX, modY, modZ).getType() != Material.IRON_FENCE)
            return 0;

        int ironBarCount;
        for (ironBarCount = 1; currentBlock.getRelative(0, 1, 0).getType() == Material.IRON_FENCE; ironBarCount++) {
            currentBlock = currentBlock.getRelative(0, 1, 0);
        }

        return ironBarCount;
    }

    private boolean checkIfPlayerFrequencyMatches(final Player player, final Radio radio) {
        // Checks to make sure the player is holding a compass.
        if (!player.getInventory().contains(pipboyId))
            return false;

        // Retrieves the frequency the player is currently listening on.
        final Frequency playerFreq = infoManager.getFrequency(player.getName());

        // If the player does not have a frequency, the frequencies do not match
        // up.
        if (playerFreq.isOff())
            return false;

        // If the player's frequency is currently set to "scan", returns a
        // randomized chance that the frequencies match, based on the scanChance
        // value.
        if (playerFreq.isScanning()) {
            final double random = Math.random();
            return random <= scanChance;
        }

        // Returns whether or not the player's and the radio's frequencies match
        // up.
        return playerFreq.equals(radio.getFrequency());
    }
}
