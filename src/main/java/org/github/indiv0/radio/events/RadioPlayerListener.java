package org.github.indiv0.radio.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.github.indiv0.radio.blah.RadioPlugin;

public class RadioPlayerListener implements Listener {
    RadioPlugin plugin;

    public RadioPlayerListener(RadioPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Checks to see if the world from which the event was sent supports
        // radio broadcasting.
        if (!plugin.getOn().get(event.getPlayer().getWorld())) { return; }

        // Confirms that the action performed by the player was a right click.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }

        // Confirms that the player is holding a COMPASS.
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.COMPASS) { return; }

        // Confirms that the block is a JUKEBOX.
        Block block = event.getClickedBlock();
        if (block.getType() != Material.JUKEBOX) { return; }

        // Checks to make sure that the player was holding something in his
        // hand.
        if (player.getItemInHand() == null) { return; }

        // Checks that the block which was clicked is recieving a redstone
        // current.
        if (!(block.getBlockPower(BlockFace.NORTH) > 0 || block.getBlockPower(BlockFace.SOUTH) > 0 || block.getBlockPower(BlockFace.EAST) > 0 || block.getBlockPower(BlockFace.WEST) > 0)) { return; }

        // Sets the target of the player's compass to the radio.
        player.setCompassTarget(block.getLocation());
    }
}