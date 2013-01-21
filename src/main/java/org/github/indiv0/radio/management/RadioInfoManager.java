package org.github.indiv0.radio.management;

import java.math.BigDecimal;

import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;
import org.github.indiv0.radio.util.FrequencyConstructorFactory;
import org.github.indiv0.radio.util.SupplimentaryTypes;

import ashulman.mbapi.management.InfoManager;
import ashulman.mbapi.storage.TypeSafeStorageMap;
import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeSet;

public class RadioInfoManager extends InfoManager {

    private final TypeSafeStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    private final FrequencyConstructorFactory FREQUENCY_FACTORY = new FrequencyConstructorFactory();

    public RadioInfoManager(ConfigurationContext configurationContext) {
        super(configurationContext);

        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getStorageMap("frequencies", SupplimentaryTypes.FREQUENCY);
        radios = storageManager.getStorageSet("radios", SupplimentaryTypes.RADIO);

        radios.load();

        registerPlayerInfoLoader(frequencies, FREQUENCY_FACTORY);
    }

    public TypeSafeStorageSet<Radio> getRadios() {
        return radios;
    }

    public Frequency getFrequency(final String playerName) {
        if (frequencies.get(playerName) == null) {
            frequencies.load(playerName, FREQUENCY_FACTORY);
        }

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
