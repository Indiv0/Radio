package in.nikitapek.radio.events;

import com.amshulman.typesafety.TypeSafeSet;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import in.nikitapek.radio.util.RadioConfigurationContext;
import in.nikitapek.radio.util.RadioUtil;
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

public class RadioListener implements Listener {
    private final TypeSafeSet<Radio> radios;
    private final TypeSafeSet<World> broadcastingWorlds;

    public RadioListener(RadioConfigurationContext configurationContext) {
        radios = configurationContext.infoManager.getRadios();
        broadcastingWorlds = configurationContext.broadcastingWorlds;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
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
        if (radioBlock.getRelative(1, 0, 0).getType() == Material.JUKEBOX) {
            radioBlock = radioBlock.getRelative(1, 0, 0);
        } else if (radioBlock.getRelative(-1, 0, 0).getType() == Material.JUKEBOX) {
            radioBlock = radioBlock.getRelative(-1, 0, 0);
        } else if (radioBlock.getRelative(0, 0, 1).getType() == Material.JUKEBOX) {
            radioBlock = radioBlock.getRelative(0, 0, 1);
        } else if (radioBlock.getRelative(0, 0, -1).getType() == Material.JUKEBOX) {
            radioBlock = radioBlock.getRelative(0, 0, -1);
        } else if (event.getBlock().getRelative(0, 1, 0).getType() == Material.JUKEBOX) {
            radioBlock = radioBlock.getRelative(0, 1, 0);
        } else {
            return;
        }

        // Defines the location of the radio.
        Location location = radioBlock.getLocation();

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
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Checks to see if the block being broken is a JUKEBOX.
        if (block.getType() != Material.JUKEBOX) {
            return;
        }

        Location location = block.getLocation();

        // Checks if the JUKEBOX is a radio.
        if (getRadioByLocation(location) == null) {
            return;
        }

        // Removes the radio.
        radios.remove((getRadioByLocation(location)));
    }

    public Radio getRadioByLocation(Location location) {
        for (Radio radio : radios) {
            if (radio.getLocation().equals(location)) {
                return radio;
            }
        }
        return null;
    }
}
