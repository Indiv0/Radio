package org.github.indiv0.radio.storage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.github.indiv0.radio.main.DoubleConstructorFactory;

import ashulman.mbapi.storage.StorageManager;
import ashulman.mbapi.storage.TypeSafeStorageMap;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeMap;
import ashulman.typesafety.TypeSafeSet;

import com.google.gson.reflect.TypeToken;

public class InfoManager {
    private final StorageManager storageManager;
    private final TypeSafeMap<String, Double> frequencies;

    public InfoManager(ConfigurationContext configurationContext) {
        // Define the storage manager which will handle the storage.
        storageManager = configurationContext.storageManager;
        // Retrieves the storage map for "frequencies".
        frequencies = storageManager.getStorageMap("frequencies", new TypeToken<Double>() {
        }.getType());

        // Registers an event handler for "frequencies".
        configurationContext.plugin.registerEventHandler(new FrequencyLoader((TypeSafeStorageMap<Double>) frequencies));
    }

    public Double getPlayerFrequency(String playerName) {
        return frequencies.get(playerName);
    }

    public TypeSafeSet<String> getPlayersWithFrequencies() {
        return frequencies.keySet();
    }

    public void setFrequency(String playerName, Double frequency) {
        ((TypeSafeStorageMap<Double>) frequencies).put(playerName, frequency);
    }

    public void saveAll() {
        for (String playerName : frequencies.keySet()) {
            ((TypeSafeStorageMap<Double>) frequencies).save(playerName);
        }
    }

    public void unloadAll() {
        for (String playerName : frequencies.keySet()) {
            ((TypeSafeStorageMap<Double>) frequencies).unload(playerName);
        }
    }

    private static class FrequencyLoader implements Listener {
        private final TypeSafeStorageMap<Double> map;
        public static final DoubleConstructorFactory FACTORY = new DoubleConstructorFactory();

        public FrequencyLoader(TypeSafeStorageMap<Double> map) {
            this.map = map;
        }

        @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerLogin(PlayerLoginEvent event) {
            map.load(event.getPlayer().getName(), FACTORY);
        }

        @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent event) {
            map.unload(event.getPlayer().getName());
        }
    }
}
