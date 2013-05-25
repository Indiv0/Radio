package in.nikitapek.radio.management;

import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import in.nikitapek.radio.util.FrequencyConstructorFactory;
import in.nikitapek.radio.util.SupplimentaryTypes;

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

import com.amshulman.mbapi.management.InfoManager;
import com.amshulman.mbapi.storage.TypeSafeStorageMap;
import com.amshulman.mbapi.storage.TypeSafeStorageSet;
import com.amshulman.mbapi.util.ConfigurationContext;
import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.TypeSafeSet;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;

public final class RadioInfoManager extends InfoManager {
    private static final FrequencyConstructorFactory FREQUENCY_FACTORY = new FrequencyConstructorFactory();

    private final TypeSafeMap<BigDecimal, TypeSafeSet<Player>> listenerMap = new TypeSafeMapImpl<BigDecimal, TypeSafeSet<Player>>(new HashMap<BigDecimal, TypeSafeSet<Player>>(), SupplimentaryTypes.BIGDECIMAL, SupplimentaryTypes.TREESET);

    private final TypeSafeStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    public RadioInfoManager(final ConfigurationContext configurationContext) {
        super(configurationContext);

        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getStorageMap("frequencies", SupplimentaryTypes.FREQUENCY);
        radios = storageManager.getStorageSet("radios", SupplimentaryTypes.RADIO);
        registerPlayerInfoLoader(frequencies, FREQUENCY_FACTORY);
        configurationContext.plugin.registerEventHandler(new ListenerLoader());

        radios.load();

        // load any players already on the server -- in case of reload
        for (final Player player : Bukkit.getOnlinePlayers()) {
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

    private Frequency getFrequency(final String playerName) {
        if (frequencies.get(playerName) == null) {
            frequencies.load(playerName, FREQUENCY_FACTORY);
        }
        return frequencies.get(playerName);
    }

    public TypeSafeSet<Radio> getRadios() {
        return radios;
    }

    public void setFrequency(final Player player, final BigDecimal frequency) {
        final Frequency f = getFrequency(player.getName());

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

    public TypeSafeSet<Player> getListeners(final BigDecimal frequency) {
        return listenerMap.get(frequency);
    }

    private class ListenerLoader implements Listener {
        public ListenerLoader() {}

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerFinishLogin(final PlayerLoginEvent event) {
            if (Result.ALLOWED.equals(event.getResult())) {
                final BigDecimal frequency = getFrequency(event.getPlayer().getName()).getFrequency();
                TypeSafeSet<Player> listeners = listenerMap.get(frequency);

                if (listeners == null) {
                    listeners = new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER);
                    listenerMap.put(frequency, listeners);
                }

                listeners.add(event.getPlayer());
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerQuit(final PlayerQuitEvent event) {
            listenerMap.get(getFrequency(event.getPlayer().getName()).getFrequency()).remove(event.getPlayer());
        }
    }
}
