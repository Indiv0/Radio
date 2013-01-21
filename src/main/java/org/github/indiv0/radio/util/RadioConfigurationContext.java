package org.github.indiv0.radio.util;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.github.indiv0.radio.management.RadioInfoManager;
import org.github.indiv0.radio.serialization.FrequencyTypeAdapter;
import org.github.indiv0.radio.serialization.Radio;

import ashulman.mbapi.plugin.MbapiPlugin;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeSet;
import ashulman.typesafety.gson.TypeSafeSetTypeAdapter;
import ashulman.typesafety.impl.TypeSafeSetImpl;

public class RadioConfigurationContext extends ConfigurationContext {

    public final RadioInfoManager infoManager;

    public final TypeSafeSet<World> broadcastingWorlds;
    public final int pipboyId;
    public final double scanChance;
    public final int ironBarExtension;
    public final HashMap<Material, Double> signalClarityBlocks = new HashMap<Material, Double>();
    public final boolean wallRadioPersist;
    public final boolean userRadioPersist;
    public final boolean transmitEmptyMessages;

    public RadioConfigurationContext(MbapiPlugin plugin) {
        super(plugin, new TypeSafeSetTypeAdapter<Radio>(SupplimentaryTypes.TREESET, SupplimentaryTypes.RADIO), new FrequencyTypeAdapter());

        infoManager = new RadioInfoManager(this);

        plugin.saveDefaultConfig();

        // Tries to load the configuration from the file into configYaml.
        YamlConfiguration configYaml = (YamlConfiguration) plugin.getConfig();

        // Retrieves the worlds in which frequency scanning is enabled.
        broadcastingWorlds = new TypeSafeSetImpl<World>(new HashSet<World>(), SupplimentaryTypes.WORLD);
        for (final World world : Bukkit.getWorlds()) {
            if (configYaml.getBoolean("worlds." + world.getName(), false)) {
                broadcastingWorlds.add(world);
            }
        }

        // Retrieves the "pipboy" item ID.
        pipboyId = configYaml.getInt("pipboyID", 345);

        // Retrieves the chance for a player on the "scan" frequency to receive a broadcast.
        scanChance = configYaml.getDouble("scanChance", 0.01);

        // Retrieves the chance for a player on the "scan" frequency to receive a broadcast.
        ironBarExtension = configYaml.getInt("ironBarExtension", 30);

        ConfigurationSection configSection = configYaml.getConfigurationSection("signalClarityBlocks");
        for (String key : configSection.getKeys(false))
            signalClarityBlocks.put(Material.getMaterial(key), configSection.getDouble(key));

        wallRadioPersist = configYaml.getBoolean("wallRadioPersist", true);
        userRadioPersist = configYaml.getBoolean("userRadioPersist", true);
        transmitEmptyMessages = configYaml.getBoolean("transmitEmptyMessages", false);
    }
}
