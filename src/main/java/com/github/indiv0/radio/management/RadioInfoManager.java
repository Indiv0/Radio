package com.github.indiv0.radio.management;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import ashulman.mbapi.management.InfoManager;
import ashulman.mbapi.storage.TypeSafeStorageMap;
import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.mbapi.util.CoreTypes;
import ashulman.typesafety.TypeSafeMap;
import ashulman.typesafety.TypeSafeSet;
import ashulman.typesafety.impl.TypeSafeMapImpl;
import ashulman.typesafety.impl.TypeSafeSetImpl;

import com.github.indiv0.radio.serialization.Frequency;
import com.github.indiv0.radio.serialization.Radio;
import com.github.indiv0.radio.util.FrequencyConstructorFactory;
import com.github.indiv0.radio.util.SupplimentaryTypes;

public class RadioInfoManager extends InfoManager {

    private final TypeSafeMap<BigDecimal, TypeSafeSet<Player>> listenerMap = new TypeSafeMapImpl<BigDecimal, TypeSafeSet<Player>>(new HashMap<BigDecimal, TypeSafeSet<Player>>(), SupplimentaryTypes.BIGDECIMAL, SupplimentaryTypes.TREESET);

    private final TypeSafeStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    private static final FrequencyConstructorFactory FREQUENCY_FACTORY = new FrequencyConstructorFactory();

    public RadioInfoManager(ConfigurationContext configurationContext) {
        super(configurationContext);

        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getStorageMap("frequencies", SupplimentaryTypes.FREQUENCY);
        radios = storageManager.getStorageSet("radios", SupplimentaryTypes.RADIO);
        registerPlayerInfoLoader(frequencies, FREQUENCY_FACTORY);
        configurationContext.plugin.registerEventHandler(new ListenerLoader());

        radios.load();

        // load any players already on the server -- in case of reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            frequencies.load(player.getName(), FREQUENCY_FACTORY);
        }

        listenerMap.put(Frequency.OFF, new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER));
        listenerMap.put(Frequency.SCANNING, new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER));
    }

    @Override
    public void saveAll() {
        frequencies.saveAll();
        radios.saveAll();
    }

    @Override
    public void unloadAll() {
        frequencies.unloadAll();
        radios.unloadAll();
    }

    private Frequency getFrequency(String playerName) {
        if (frequencies.get(playerName) == null) {
            frequencies.load(playerName, FREQUENCY_FACTORY);
        }
        return frequencies.get(playerName);
    }

    public TypeSafeSet<Radio> getRadios() {
        return radios;
    }

    public void setFrequency(Player player, BigDecimal frequency) {
        Frequency f = getFrequency(player.getName());

        TypeSafeSet<Player> listeners = listenerMap.get(f.getFrequency());
        if (listeners != null) {
            listeners.remove(player);
        }

        listeners = listenerMap.get(frequency);
        if (listeners == null) {
            listeners = new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER);
            listenerMap.put(frequency, listeners);
        }
        listeners.add(player);

        f.setFrequency(frequency);
    }

    public TypeSafeSet<Player> getListeners(BigDecimal frequency) {
        return listenerMap.get(frequency);
    }

    private class ListenerLoader implements Listener {
        public ListenerLoader() {}

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerFinishLogin(PlayerLoginEvent event) {
            if (Result.ALLOWED.equals(event.getResult())) {
                BigDecimal frequency = getFrequency(event.getPlayer().getName()).getFrequency();
                TypeSafeSet<Player> listeners = listenerMap.get(frequency);

                if (listeners == null) {
                    listeners = new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER);
                    listenerMap.put(frequency, listeners);
                }

                listeners.add(event.getPlayer());
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent event) {
            listenerMap.get(getFrequency(event.getPlayer().getName()).getFrequency()).remove(event.getPlayer());
        }
    }
}
