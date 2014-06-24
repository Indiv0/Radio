package in.nikitapek.radio.management;

import com.amshulman.mbapi.management.InfoManager;
import com.amshulman.mbapi.storage.TypeSafeDistributedStorageMap;
import com.amshulman.mbapi.storage.TypeSafeStorageSet;
import com.amshulman.mbapi.util.ConfigurationContext;
import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.TypeSafeSet;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import in.nikitapek.radio.util.FrequencyConstructorFactory;
import in.nikitapek.radio.util.ScaleInvariantBigDecimal;
import in.nikitapek.radio.util.SupplementaryTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;

public class RadioInfoManager extends InfoManager {
    private static final FrequencyConstructorFactory FREQUENCY_FACTORY = new FrequencyConstructorFactory();

    private final TypeSafeMap<ScaleInvariantBigDecimal, TypeSafeSet<Player>> listenerMap = new TypeSafeMapImpl<>(new HashMap<ScaleInvariantBigDecimal, TypeSafeSet<Player>>(), SupplementaryTypes.LARGEDECIMAL, SupplementaryTypes.TREESET);

    private final TypeSafeDistributedStorageMap<Frequency> frequencies;
    private final TypeSafeStorageSet<Radio> radios;

    public RadioInfoManager(ConfigurationContext configurationContext) {
        super(configurationContext);

        // Retrieves the storage map for "frequencies" and "radios".
        frequencies = storageManager.getDistributedStorageMap("frequencies", SupplementaryTypes.FREQUENCY);
        radios = storageManager.getStorageSet("radios", SupplementaryTypes.RADIO);
        registerPlayerInfoLoader(frequencies, FREQUENCY_FACTORY);
        configurationContext.plugin.registerEventHandler(new ListenerLoader());

        radios.loadAll();

        // load any players already on the server -- in case of reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            frequencies.load(player.getName(), FREQUENCY_FACTORY);
        }

        listenerMap.put(Frequency.OFF, new TypeSafeSetImpl<>(new HashSet<Player>(), CoreTypes.PLAYER));
        listenerMap.put(Frequency.SCANNING, new TypeSafeSetImpl<>(new HashSet<Player>(), CoreTypes.PLAYER));
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

    private Frequency getFrequency(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (frequencies.get(playerUUID) == null) {
            frequencies.load(playerUUID, FREQUENCY_FACTORY);
        }
        return frequencies.get(playerUUID);
    }

    public TypeSafeSet<Radio> getRadios() {
        return radios;
    }

    public void setFrequency(Player player, ScaleInvariantBigDecimal frequency) {
        // Retrieves the frequency the player is currently tuned to.
        Frequency f = getFrequency(player);

        // Gets the list of players currently listening on the frequency the player is tuned to.
        TypeSafeSet<Player> listeners = listenerMap.get(f.getFrequency());
        if (listeners != null) {
            listeners.remove(player);
        }

        // Retrieves the list of players currently tuned to the frequency provided.
        listeners = listenerMap.get(frequency);
        // If no players are tuned into the frequency provided, then the set containing these players is created and added to the listenerMap.
        if (listeners == null) {
            listeners = new TypeSafeSetImpl<>(new HashSet<Player>(), CoreTypes.PLAYER);
            listenerMap.put(frequency, listeners);
        }

        // The player is added to the list of players currently tuned to the provided frequency.
        listeners.add(player);
        // The player's frequency is set the the provided frequency.
        f.setFrequency(frequency);
    }

    public TypeSafeSet<Player> getListeners(ScaleInvariantBigDecimal frequency) {
        return listenerMap.get(frequency);
    }

    private class ListenerLoader implements Listener {
        public ListenerLoader() {
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerFinishLogin(PlayerLoginEvent event) {
            if (Result.ALLOWED.equals(event.getResult())) {
                ScaleInvariantBigDecimal frequency = getFrequency(event.getPlayer()).getFrequency();
                TypeSafeSet<Player> listeners = listenerMap.get(frequency);

                if (listeners == null) {
                    listeners = new TypeSafeSetImpl<>(new HashSet<Player>(), CoreTypes.PLAYER);
                    listenerMap.put(frequency, listeners);
                }

                listeners.add(event.getPlayer());
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent event) {
            listenerMap.get(getFrequency(event.getPlayer()).getFrequency()).remove(event.getPlayer());
        }
    }
}
