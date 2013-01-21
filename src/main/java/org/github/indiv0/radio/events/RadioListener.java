package org.github.indiv0.radio.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;
import org.github.indiv0.radio.util.RadioConfigurationContext;
import org.github.indiv0.radio.util.RadioConstructorFactory;
import org.github.indiv0.radio.util.RadioUtil;

import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.typesafety.TypeSafeSet;

public class RadioListener implements Listener {
    private final TypeSafeStorageSet<Radio> radios;
    private final TypeSafeSet<World> broadcastingWorlds;

    public static final RadioConstructorFactory FACTORY = new RadioConstructorFactory();

    public RadioListener(final RadioConfigurationContext configurationContext) {
        radios = configurationContext.infoManager.getRadios();
        broadcastingWorlds = configurationContext.broadcastingWorlds;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstoneChange(final BlockRedstoneEvent event) {
        // Checks to make sure the current has increased since the last redstone state.
        if (event.getOldCurrent() >= event.getNewCurrent()) {
            return;
        }

        // Checks to see if the world from which the event was sent supports radio broadcasting.
        if (broadcastingWorlds.contains(event.getBlock().getWorld())) {
            return;
        }

        // Gets the block representing the radio, if it exists. It will also return if the block being powered is one of the signs on the JUKEBOX.
        Block radioBlock = event.getBlock();
        if (event.getBlock().getRelative(1, 0, 0).getType() == Material.JUKEBOX) {
            radioBlock = event.getBlock().getRelative(1, 0, 0);
        } else if (event.getBlock().getRelative(-1, 0, 0).getType() == Material.JUKEBOX) {
            radioBlock = event.getBlock().getRelative(-1, 0, 0);
        } else if (event.getBlock().getRelative(0, 0, 1).getType() == Material.JUKEBOX) {
            radioBlock = event.getBlock().getRelative(0, 0, 1);
        } else if (event.getBlock().getRelative(0, 0, -1).getType() == Material.JUKEBOX) {
            radioBlock = event.getBlock().getRelative(0, 0, -1);
        } else if (event.getBlock().getRelative(0, 1, 0).getType() == Material.JUKEBOX) {
            radioBlock = event.getBlock().getRelative(0, 1, 0);
        } else {
            return;
        }
        
        // Defines the location of the radio.
        final Location location = radioBlock.getLocation();

        // Creates the radio.
        for (BlockFace face : BlockFace.values()) {
            if (!RadioUtil.signHasValidFrequency(location, face)) {
                continue;
            }
            // Adds the radio to the radios list.
            Radio radio = new Radio(location, new Frequency(RadioUtil.getFrequencyFromStringWithoutTags(Radio.getSign(location, face).getLine(0))));

            if (getRadioByLocation(radio.getLocation()) != null) {
                radios.remove(getRadioByLocation(radio.getLocation()));
            }

            radios.add(radio);
            break;
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();

        // Checks to see if the block being broken is a JUKEBOX.
        if (block.getType() != Material.JUKEBOX) {
            return;
        }

        final Location location = block.getLocation();

        // Checks if the JUKEBOX is a radio.
        if (getRadioByLocation(location) == null) {
            return;
        }

        // Removes the radio.
        radios.remove((getRadioByLocation(location)));
    }

    public Radio getRadioByLocation(final Location location) {
        for (final Radio radio : radios) {
            if (radio.getLocation().equals(location)) {
                return radio;
            }
        }
        return null;
    }
}
