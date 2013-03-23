package com.github.indiv0.radio.management;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ashulman.mbapi.management.InfoManager;
import ashulman.mbapi.storage.TypeSafeStorageMap;
import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeSet;

import com.github.indiv0.radio.serialization.Frequency;
import com.github.indiv0.radio.serialization.Radio;
import com.github.indiv0.radio.util.FrequencyConstructorFactory;
import com.github.indiv0.radio.util.SupplimentaryTypes;

public class RadioInfoManager extends InfoManager {

    private final TypeSafeStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    private static final FrequencyConstructorFactory FREQUENCY_FACTORY = new FrequencyConstructorFactory();

    public RadioInfoManager(ConfigurationContext configurationContext) {
        super(configurationContext);

        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getStorageMap("frequencies", SupplimentaryTypes.FREQUENCY);
        radios = storageManager.getStorageSet("radios", SupplimentaryTypes.RADIO);
        registerPlayerInfoLoader(frequencies, FREQUENCY_FACTORY);
        radios.load();

        // load any players already on the server -- in case of reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            frequencies.load(player.getName(), FREQUENCY_FACTORY);
        }
    }

    public TypeSafeStorageSet<Radio> getRadios() {
        return radios;
    }

    public Frequency getFrequency(final String playerName) {
        return frequencies.get(playerName);
    }

    public void setRadio(final Radio radio) {
        radios.add(radio);
    }

    public void setFrequency(final String playerName, final BigDecimal frequency) {
        if (frequencies.get(playerName) == null) {
            frequencies.load(playerName, FREQUENCY_FACTORY);
        }

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
}
