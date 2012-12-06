package org.github.indiv0.radio.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.main.RadioPlugin;

public class RadioBlockListener implements Listener {
    RadioPlugin plugin;

    public RadioBlockListener(RadioPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        // Checks to make sure the current has increased since the last redstone
        // state.
        if (event.getOldCurrent() >= event.getNewCurrent()) return;

        // Checks to see if the world from which the event was sent supports
        // radio broadcasting.
        if (!plugin.getOn().get(event.getBlock().getWorld())) { return; }

        // Gets the block representing the radio, if it exists.
        // It will also return if the block being powered is one of the signs on
        // the JUKEBOX.
        Block radioBlock = event.getBlock();
        if (event.getBlock().getRelative(1, 0, 0).getType() == Material.JUKEBOX)
            radioBlock = event.getBlock().getRelative(1, 0, 0);
        else if (event.getBlock().getRelative(-1, 0, 0).getType() == Material.JUKEBOX)
            radioBlock = event.getBlock().getRelative(-1, 0, 0);
        else if (event.getBlock().getRelative(0, 0, 1).getType() == Material.JUKEBOX)
            radioBlock = event.getBlock().getRelative(0, 0, 1);
        else if (event.getBlock().getRelative(0, 0, -1).getType() == Material.JUKEBOX)
            radioBlock = event.getBlock().getRelative(0, 0, -1);
        else if (event.getBlock().getType() == Material.WALL_SIGN)
            return;
        else
            return;

        // Defines the location of the radio.
        Location location = radioBlock.getLocation();

        // Adds the radio to the radios list.
        plugin.addRadio(location);

        RadioBroadcast.attemptBroadcast(location);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Checks to see if the block being broken is a JUKEBOX.
        if (event.getBlock().getType() != Material.JUKEBOX) return;

        // Checks if the JUKEBOX is a radio.
        if (!plugin.getRadios().containsKey(event.getBlock().getLocation())) return;

        // Removes the radio.
        plugin.removeRadio(event.getBlock().getLocation());
    }
}