package org.github.indiv0.radio.storage;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.github.indiv0.radio.main.FrequencyConstructorFactory;
import org.github.indiv0.radio.main.RadioConstructorFactory;
import org.github.indiv0.radio.main.RadioPlugin;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;
import org.github.indiv0.radio.util.RadioUtil;

import ashulman.mbapi.storage.StorageManager;
import ashulman.mbapi.storage.TypeSafeStorageMap;
import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeSet;

import com.google.gson.reflect.TypeToken;

public class InfoManager {
    private final StorageManager storageManager;
    private final TypeSafeStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    public InfoManager(final ConfigurationContext configurationContext) {
        // Define the storage manager which will handle the storage.
        storageManager = configurationContext.storageManager;
        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getStorageMap("frequencies", new TypeToken<Frequency>() {
        }.getType());
        radios = storageManager.getStorageSet("radios", new TypeToken<Radio>() {
        }.getType());

        // Registers an event handler for "frequencies" and "radios".
        configurationContext.plugin.registerEventHandler(new FrequencyLoader(frequencies));
        configurationContext.plugin.registerEventHandler(new RadioLoader(radios, configurationContext));
    }

    public TypeSafeStorageSet<Radio> getRadios() {
        return radios;
    }

    public Frequency getFrequency(final String playerName) {
        if (frequencies.get(playerName) == null)
            frequencies.load(playerName, FrequencyLoader.FACTORY);

        return frequencies.get(playerName);
    }

    public void setRadio(final Radio radio) {
        radios.add(radio);
    }

    public void setFrequency(final String playerName, final BigDecimal frequency) {
        if (frequencies.get(playerName) == null)
            frequencies.load(playerName, FrequencyLoader.FACTORY);

        frequencies.get(playerName).setFrequency(frequency);
    }

    public void saveAll() {
        for (final String playerName : frequencies.keySet()) {
            frequencies.save(playerName);
        }
        radios.save();
    }

    public void unloadAll() {
        for (final String playerName : frequencies.keySet()) {
            frequencies.unload(playerName);
        }
        radios.unload();
    }

    public TypeSafeSet<String> getPlayersWithFrequencies() {
        return frequencies.keySet();
    }

    private static class FrequencyLoader implements Listener {
        private final TypeSafeStorageMap<Frequency> map;
        public static final FrequencyConstructorFactory FACTORY = new FrequencyConstructorFactory();

        public FrequencyLoader(final TypeSafeStorageMap<Frequency> map) {
            this.map = map;
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerLogin(final PlayerLoginEvent event) {
            map.load(event.getPlayer().getName(), FrequencyLoader.FACTORY);
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerQuit(final PlayerQuitEvent event) {
            map.unload(event.getPlayer().getName());
        }
    }

    private static class RadioLoader implements Listener {
        private final TypeSafeStorageSet<Radio> map;
        private final ConfigurationContext configurationContext;

        public static final RadioConstructorFactory FACTORY = new RadioConstructorFactory();

        public RadioLoader(final TypeSafeStorageSet<Radio> map,
                final ConfigurationContext configurationContext) {
            this.map = map;
            this.configurationContext = configurationContext;
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPluginEnable(final PluginEnableEvent event) {
            if (event.getPlugin() instanceof RadioPlugin)
                map.load();
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPluginDisable(final PluginDisableEvent event) {
            if (event.getPlugin() instanceof RadioPlugin)
                map.unload();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBlockRedstoneChange(final BlockRedstoneEvent event) {
            // Checks to make sure the current has increased since the last
            // redstone state.
            if (event.getOldCurrent() >= event.getNewCurrent())
                return;

            // Checks to see if the world from which the event was sent supports
            // radio broadcasting.
            if (!((RadioPlugin) configurationContext.plugin).getBroadcastingWorlds().get(event.getBlock().getWorld()))
                return;

            // Gets the block representing the radio, if it exists.
            // It will also return if the block being powered is one of the
            // signs on
            // the JUKEBOX.
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
            } else if (event.getBlock().getType() == Material.WALL_SIGN)
                return;
            else
                return;

            // Defines the location of the radio.
            final Location location = radioBlock.getLocation();

            // Creates the radio.
            for (BlockFace face : BlockFace.values()) {
                if (!RadioUtil.signHasValidFrequency(location, face))
                    continue;

                // Adds the radio to the radios list.
                Radio radio = new Radio(location, new Frequency(RadioUtil.getFrequencyFromStringWithoutTags(Radio.getSign(location, face).getLine(0))));

                if (getRadioByLocation(radio.getLocation()) != null) {
                    map.remove(getRadioByLocation(radio.getLocation()));
                }

                map.add(radio);
                break;
            }
        }

        @EventHandler
        public void onBlockBreak(final BlockBreakEvent event) {
            final Block block = event.getBlock();

            // Checks to see if the block being broken is a JUKEBOX.
            if (block.getType() != Material.JUKEBOX)
                return;

            final Location location = block.getLocation();

            // Checks if the JUKEBOX is a radio.
            if (getRadioByLocation(location) == null)
                return;

            // Removes the radio.
            map.remove((getRadioByLocation(location)));
        }

        public Radio getRadioByLocation(final Location location) {
            for (final Radio radio : map)
                if (radio.getLocation().equals(location))
                    return radio;

            return null;
        }
    }
}
