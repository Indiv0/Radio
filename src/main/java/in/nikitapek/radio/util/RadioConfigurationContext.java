package in.nikitapek.radio.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;
import com.amshulman.typesafety.TypeSafeSet;
import com.amshulman.typesafety.gson.TypeSafeSetTypeAdapter;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.FrequencyTypeAdapter;
import in.nikitapek.radio.serialization.LocationTypeAdapter;
import in.nikitapek.radio.serialization.Radio;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

public final class RadioConfigurationContext extends ConfigurationContext {

    public final RadioInfoManager infoManager;

    public final TypeSafeSet<World> broadcastingWorlds;
    public final int pipboyId;
    public final double scanChance;
    public final int ironBarExtension;
    public static final Map<Material, Double> signalClarityBlocks = new HashMap<>();
    public final boolean wallRadioPersist;
    public final boolean userRadioPersist;
    public final boolean transmitEmptyMessages;
    public final long transmitDelay;
    public final String privateSalt;

    public RadioConfigurationContext(MbapiPlugin plugin) {
        super(plugin, new TypeSafeSetTypeAdapter<Radio>(SupplementaryTypes.TREESET, SupplementaryTypes.RADIO), new LocationTypeAdapter(), new FrequencyTypeAdapter());

        plugin.saveDefaultConfig();

        // Tries to load the configuration from the file into configYaml.
        YamlConfiguration configYaml = (YamlConfiguration) plugin.getConfig();

        // Retrieves the worlds in which frequency scanning is enabled.
        broadcastingWorlds = new TypeSafeSetImpl<>(new HashSet<World>(), SupplementaryTypes.WORLD);
        for (World world : Bukkit.getWorlds()) {
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

        ConfigurationSection configSection = configYaml.getConfigurationSection("signalClarityBlocks");
        for (String key : configSection.getKeys(false)) {
            signalClarityBlocks.put(Material.getMaterial(key), configSection.getDouble(key));
        }

        wallRadioPersist = configYaml.getBoolean("wallRadioPersist", true);
        userRadioPersist = configYaml.getBoolean("userRadioPersist", true);
        transmitEmptyMessages = configYaml.getBoolean("transmitEmptyMessages", false);

        transmitDelay = configYaml.getLong("transmitDelay", 100L);

        // Retrieve the bcrypt salt, generating it if necessary.
        File saltFile = new File("./plugins/Radio/salt");

        if (!saltFile.exists()) {
            try {
                saltFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to create salt file");
            }
        }

        String tmpSalt = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(saltFile.getAbsoluteFile()));
            tmpSalt = in.readLine();
            in.close();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to read salt file");
        }

        if ("".equals(tmpSalt) || tmpSalt == null) {
            // Generate a secure BCrypt salt, using 12 rounds.
            tmpSalt = BCrypt.gensalt(12);

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(saltFile.getAbsoluteFile()));
                out.write(tmpSalt);
                out.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to write salt file");
            }
        }
        privateSalt = tmpSalt;

        infoManager = new RadioInfoManager(this);
    }
}
