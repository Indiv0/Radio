package org.github.indiv0.radio.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class RadioBroadcast implements Runnable {
    public static RadioPlugin plugin;

    public RadioBroadcast(RadioPlugin plugin) {
        RadioBroadcast.plugin = plugin;
    }

    @Override
    public void run() {
        // Retrieves all of the available radios, making sure that the list is
        // not empty.
        HashMap<Location, String> radios = plugin.getRadios();
        if (radios == null) return;
        if (radios.size() < 1) return;

        // Iterates through every radio in the list.
        for (Entry<Location, String> radio : radios.entrySet()) {
            attemptBroadcast(radio.getKey());
        }
    }

    public static void attemptBroadcast(Location location) {
        // Retrieves the block which represents the radio.
        Block radioBlock = location.getBlock();

        // Moves on to the next radio if the current one is not powered.
        if (!radioBlock.isBlockIndirectlyPowered()) return;

        // Checks to make sure the radio has signs on it.
        if (radioBlock.getRelative(BlockFace.NORTH).getType() != Material.WALL_SIGN &&
                radioBlock.getRelative(BlockFace.EAST).getType() != Material.WALL_SIGN &&
                radioBlock.getRelative(BlockFace.SOUTH).getType() != Material.WALL_SIGN &&
                radioBlock.getRelative(BlockFace.WEST).getType() != Material.WALL_SIGN) return;

        // Checks to see if the radio has a clarity enchancing block (e.g.
        // Iron, Gold, Lapus, or Diamond).
        Material baseMaterial = Material.DIRT;
        if (radioBlock.getRelative(0, 1, 0).getType() == Material.IRON_BLOCK ||
                radioBlock.getRelative(0, 1, 0).getType() == Material.GOLD_BLOCK ||
                radioBlock.getRelative(0, 1, 0).getType() == Material.LAPIS_BLOCK ||
                radioBlock.getRelative(0, 1, 0).getType() == Material.DIAMOND_BLOCK) {
            baseMaterial = radioBlock.getRelative(0, 1, 0).getType();
            radioBlock = radioBlock.getRelative(0, 1, 0);
        }

        // Gets the broadcastGarble, which represents the clarity of the
        // signal, as affected by the clarity enchanting block.
        double broadcastGarble = 0;
        if (baseMaterial == Material.IRON_BLOCK)
            broadcastGarble = 0.15;
        else if (baseMaterial == Material.LAPIS_BLOCK)
            broadcastGarble = 0.3;
        else if (baseMaterial == Material.GOLD_BLOCK)
            broadcastGarble = 0.5;
        else if (baseMaterial == Material.DIAMOND_BLOCK)
            broadcastGarble = 0.99;
        else if (baseMaterial == Material.DIRT)
            broadcastGarble = 0;

        // Checks how many IRON_FENCE blocks there are above the structure,
        // capping the number at 16.
        // IRON_FENCE blocks determine the range of the signal.
        int ironBarCount;
        for (ironBarCount = 0; radioBlock.getRelative(0, 1, 0).getType() == Material.IRON_FENCE && ironBarCount <= 16; ironBarCount++)
            radioBlock = radioBlock.getRelative(0, 1, 0);

        // Determines the broadcastDistance, which is a factor of the garble
        // and the IRON_FENCE count.
        double broadcastDistance = (ironBarCount * plugin.getIronBarExtension()) + (broadcastGarble * 550);

        // Retrieves the list of players who will act as recipients of the
        // message, and stores them in an array.
        List<Player> recipients = radioBlock.getWorld().getPlayers();
        Player[] recipientsArray = recipients.toArray(new Player[recipients.size()]);

        for (Player player : recipientsArray) {
            // Checks to make sure that the player and radio are in the same
            // world.
            if (player.getLocation().getWorld() != location.getWorld()) break;

            // Gets the distance of the player from the radio.
            double distance = player.getLocation().distance(location);

            // Alters the distance based on the current weather conditions in
            // the world.
            if (player.getWorld().hasStorm()) distance = 2 * distance;
            if (player.getWorld().isThundering()) distance = 0;

            // Gets the number of IRON_FENCE blocks surrounding the player, and
            // increases the strength of the signal accordingly.
            ironBarCount = 0;
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 1, 0, 0);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 0, 0, 1);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, -1, 0, 0);
            ironBarCount += calculateIronBarsSurroundingPlayer(player, 0, 0, -1);
            distance -= ironBarCount * 30;

            // Fails to broadcast if the player is beyond the broadcastDistance.
            if (distance > broadcastDistance) break;

            double garbleDistance = broadcastGarble * broadcastDistance;
            double percent = (distance - garbleDistance) / (broadcastDistance - garbleDistance);

            boolean isGarbled = (distance > garbleDistance);

            // Attempts to broadcast the messages contained on the signs on each
            // side of the block.
            broadcast(BlockFace.NORTH, location, percent, isGarbled, player);
            broadcast(BlockFace.SOUTH, location, percent, isGarbled, player);
            broadcast(BlockFace.EAST, location, percent, isGarbled, player);
            broadcast(BlockFace.WEST, location, percent, isGarbled, player);
        }
    }

    public static void broadcast(BlockFace face, Location location, double percent, boolean isGarbled, Player player) {
        // Corrects the frequency on the sign, if need be.
        Radio.registerFrequencyToSign(location, face);

        // Fails to broadcast if the frequency of the player's radio and the
        // radio that is broadcasting don't match up.
        if (!checkIfPlayerFrequencyMatches(player, plugin.getRadios().get(location))) return;

        String message = Radio.getMessage(location, face);

        // Cancels the broadcast if there is no message provided.
        if (message == null) return;

        // Garbles the message if the player is past the garbleDistance;
        if (isGarbled) message = garbleMessage(message, percent);

        player.sendMessage(ChatColor.RED + "[Radio " + Radio.convertFrequencyToIntegerNotation(plugin.getRadios().get(location)) + "] " + message);
    }

    public static String garbleMessage(String message, double percent) {
        // Garbles the message.
        int messageLength = message.length();
        int amountRemoved = (int) (percent * (messageLength));
        char[] charString = message.toCharArray();
        for (int k = 0; k < amountRemoved; k++) {
            int removalPoint = (int) (Math.random() * (charString.length - 1));
            charString[removalPoint] = ' ';
        }

        return new String(charString);
    }

    public static int calculateIronBarsSurroundingPlayer(Player player, int modX, int modY, int modZ) {
        // Gets the block at the requested offset.
        Block currentBlock = player.getLocation().getBlock().getRelative(modX, modY, modZ);

        // Checks to make sure that the block has IRON_FENCE blocks.
        if (player.getLocation().getBlock().getRelative(modX, modY, modZ).getType() != Material.IRON_FENCE) { return 0; }

        int ironBarCount;
        for (ironBarCount = 1; currentBlock.getRelative(0, 1, 0).getType() == Material.IRON_FENCE; ironBarCount++)
            currentBlock = currentBlock.getRelative(0, 1, 0);

        return ironBarCount;
    }

    private static boolean checkIfPlayerFrequencyMatches(Player player, String frequency) {
        // Checks to make sure the player is holding a compass.
        if (!player.getInventory().contains(RadioBroadcast.plugin.getPipboyID())) return false;

        // Retrieves the frequency the player is currently listening on.
        String playerFrequency = RadioBroadcast.plugin.getFrequency(player.getName());

        // If the player does not have a frequency, the frequencies do not match
        // up.
        if (playerFrequency == null) return false;

        // If the player's frequency is currently set to "scan", returns a
        // randomized chance that the frequencies match, based on the scanChance
        // value.
        if (playerFrequency.equals("scan")) {
            double random = Math.random();
            return random <= RadioBroadcast.plugin.getScanChance();
        }

        // Returns whether or not the player's and the radio's frequencies match
        // up.
        String playerFreq = Radio.convertFrequencyToIntegerNotation(playerFrequency);
        String radioFreq = Radio.convertFrequencyToIntegerNotation(frequency);

        return playerFreq.equals(radioFreq);
    }

    public static boolean isPlayerHoldingPipboy(Player player) {
        // Makes sure that the currently held item is the "Pipboy" (by default
        // the compass).
        if (player.getItemInHand().getTypeId() != plugin.getPipboyID()) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return false;
        }

        return true;
    }
}