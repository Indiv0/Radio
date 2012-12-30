package org.github.indiv0.radio.storage;

import java.math.BigDecimal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.github.indiv0.radio.main.FrequencyConstructorFactory;
import org.github.indiv0.radio.main.RadioConstructorFactory;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;

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
        configurationContext.plugin.registerEventHandler(new RadioLoader(radios));
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
        public static final RadioConstructorFactory FACTORY = new RadioConstructorFactory();

        public RadioLoader(final TypeSafeStorageSet<Radio> map) {
            this.map = map;
        }
    }
}
