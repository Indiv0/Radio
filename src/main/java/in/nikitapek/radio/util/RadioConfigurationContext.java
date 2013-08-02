package in.nikitapek.radio.util;

import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.FrequencyTypeAdapter;
import in.nikitapek.radio.serialization.LocationTypeAdapter;
import in.nikitapek.radio.serialization.Radio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;
import com.amshulman.typesafety.TypeSafeSet;
import com.amshulman.typesafety.gson.TypeSafeSetTypeAdapter;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;

public final class RadioConfigurationContext extends ConfigurationContext {

    public final RadioInfoManager infoManager;

    public final TypeSafeSet<World> broadcastingWorlds;
    public final int pipboyId;
    public final double scanChance;
    public final int ironBarExtension;
    public final Map<Material, Double> signalClarityBlocks = new HashMap<>();
    public final boolean wallRadioPersist;
    public final boolean userRadioPersist;
    public final boolean transmitEmptyMessages;

    public RadioConfigurationContext(final MbapiPlugin plugin) {
        super(plugin, new TypeSafeSetTypeAdapter<Radio>(SupplementaryTypes.TREESET, SupplementaryTypes.RADIO), new LocationTypeAdapter(), new FrequencyTypeAdapter());

        infoManager = new RadioInfoManager(this);

        plugin.saveDefaultConfig();

        // Tries to load the configuration from the file into configYaml.
        final YamlConfiguration configYaml = (YamlConfiguration) plugin.getConfig();

        // Retrieves the worlds in which frequency scanning is enabled.
        broadcastingWorlds = new TypeSafeSetImpl<>(new HashSet<World>(), SupplementaryTypes.WORLD);
        for (final World world : Bukkit.getWorlds()) {
            if (configYaml.getBoolean("worlds." + world.getName(), false)) {
                broadcastingWorlds.add(world);
            }
        }

        // Retrieves the "pipboy" item ID.
        pipboyId = configYaml.getInt("pipboyID", 345);

        // Retrieves the chance for a player on the "scan" frequency to receive a broadcast.
        scanChance = configYaml.getDouble("scanChance", 0.05d);

        // Retrieves the chance for a player on the "scan" frequency to receive a broadcast.
        ironBarExtension = configYaml.getInt("ironBarExtension", 30);

        final ConfigurationSection configSection = configYaml.getConfigurationSection("signalClarityBlocks");
        for (final String key : configSection.getKeys(false)) {
            signalClarityBlocks.put(Material.getMaterial(key), configSection.getDouble(key));
        }

        wallRadioPersist = configYaml.getBoolean("wallRadioPersist", true);
        userRadioPersist = configYaml.getBoolean("userRadioPersist", true);
        transmitEmptyMessages = configYaml.getBoolean("transmitEmptyMessages", false);
    }
}
