package org.github.indiv0.radio.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.github.indiv0.radio.commands.CommandRadio;
import org.github.indiv0.radio.commands.radio.CommandTune;
import org.github.indiv0.radio.events.RadioBlockListener;
import org.github.indiv0.radio.events.RadioPlayerListener;

import ashulman.mbapi.plugin.MbapiPlugin;
import ashulman.mbapi.util.ConfigurationContext;

public class RadioPlugin extends MbapiPlugin {
    private final RadioBroadcast broadcast = new RadioBroadcast(this);

    private final Map<World, Boolean> worldsTable = new HashMap<World, Boolean>();
    private int pipboyID = 345;
    private double scanChance = 0.01;
    private int ironBarExtension = 30;
    private HashMap<Location, String> radios;

    private ConfigurationContext configurationContext;

    public static final double OFF = 0.0;
    public static final double SCANNING = -1.0;

    @Override
    public void onEnable() {
        // Initializes the configurationContext.
        configurationContext = new ConfigurationContext(this);

        // Loads the configuration file.
        if (!loadConfig()) {
            getLogger().log(Level.WARNING, this + " has encountered an error while reading the configuration file, continuing with defaults.");
        }

        // Registers the two event handlers and the command executor.
        registerEventHandler(new RadioPlayerListener(this));
        registerEventHandler(new RadioBlockListener(this));
        registerCommandExecutor("radio", new CommandRadio(configurationContext));
        registerCommandExecutor("tune", new CommandTune(configurationContext));

        // Attempts to load the radios from the radio file.
        radios = retrieveRadios();

        // Schedules a broadcast task to handle radio message broadcasting.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L, 100L);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Cancels any tasks scheduled by this plugin.
        getServer().getScheduler().cancelTasks(this);
    }

    private boolean loadConfig() {
        // Initializes a YamlConfiguration to represent the data stored in
        // "config.yml"
        YamlConfiguration configYaml = new YamlConfiguration();

        // Makes sure the data folder exists.
        getDataFolder().mkdirs();

        // Tries to load the configuration from the file into configYaml.
        configYaml = loadConfigurationFromFile("config.yml");

        // Retieves the worlds in which frequency scanning is enabled.
        for (World world : getServer().getWorlds())
            setOn(world, configYaml.getBoolean("worlds." + world.getName(), false));

        // Retrieves the "pipboy" item ID.
        pipboyID = configYaml.getInt("pipboyID", getPipboyID());
        // Retrieves the chance for a player on the "scan" frequency to recieve
        // a broadcast.
        scanChance = configYaml.getDouble("scanChance", getScanChance());
        // Retrieves the chance for a player on the "scan" frequency to recieve
        // a broadcast.
        ironBarExtension = configYaml.getInt("ironBarExtension", getIronBarExtension());

        return true;
    }

    public YamlConfiguration loadConfigurationFromFile(String fileName) {
        getConfig();
        // Creates the data directory if it does not exist.
        getDataFolder().mkdirs();

        // Initializes the "radios.yml" file.
        File file = new File(getDataFolder(), fileName);

        if (file.exists())
            // Loads the YAML configuration from the file.
            return YamlConfiguration.loadConfiguration(file);

        // Populates it with default configuration values, if the file is
        // "config.yml"
        if (fileName.equals("config.yml")) saveDefaultConfig();

        // Loads the YAML configuration from the file.
        return YamlConfiguration.loadConfiguration(file);
    }

    public HashMap<Location, String> retrieveRadios() {
        // Creates an ArrayList to store the radios.
        HashMap<Location, String> radios = new HashMap<Location, String>();

        // Iterates through every world, obtaining frequencies.
        for (Entry<String, Object> worldEntry : getRadioYaml().getValues(false).entrySet()) {
            World world = Bukkit.getServer().getWorld(worldEntry.getKey());

            // Iterates through every frequency, adding it to the radios list.
            for (Entry<String, Object> radioEntry : getRadioYaml().getConfigurationSection(worldEntry.getKey()).getValues(false).entrySet()) {
                MemorySection positionValues = (MemorySection) radioEntry.getValue();

                Location location = new Location(world, Double.valueOf(positionValues.getString("X")), Double.valueOf(positionValues.getString("Y")), Double.valueOf(positionValues.getString("Z")));
                radios.put(location, "");
            }
        }

        // Returns the list of frequencies.
        return radios;
    }

    public void saveRadios(HashMap<Location, String> radios) {
        YamlConfiguration radioYaml = getRadioYaml();

        for (Entry<Location, String> radio : radios.entrySet()) {
            ConfigurationSection currentWorld = radioYaml.getConfigurationSection(radio.getKey().getWorld().getName());
            if (currentWorld == null) currentWorld = radioYaml.createSection(radio.getKey().getWorld().getName());

            // Create a map to store the position and frequency.
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("X", String.valueOf(radio.getKey().getX()));
            dataMap.put("Y", String.valueOf(radio.getKey().getY()));
            dataMap.put("Z", String.valueOf(radio.getKey().getZ()));
            dataMap.put("Frequency", radio.getValue());

            // Writes the data to a new section in the Yaml, accessed as the
            // hashcode of the location.
            currentWorld.createSection(Radio.getFrequencyFromLocation(radio.getKey()), dataMap);
        }

        // Attempts to save the radios to "radios.yml"
        try {
            radioYaml.save(new File(getDataFolder(), "radios.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save radios to radios.yml");
        }
    }

    public void addRadio(Location location) {
        addRadio(location, Radio.getFrequencyFromLocation(location));
    }

    public void addRadio(Location location, String frequency) {
        getRadios().put(location, frequency);
        saveRadios(getRadios());
    }

    public void removeRadio(Location location) {
        // Cancels any tasks scheduled by this plugin.
        getServer().getScheduler().cancelTasks(this);

        // Removes the radio.
        getRadios().remove(location);
        saveRadios(getRadios());

        // Schedules a broadcast task to handle radio message broadcasting.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L, 100L);
    }

    // Getter and Setter methods
    public Map<World, Boolean> getOn() {
        return worldsTable;
    }

    public void setOn(World world, boolean isOn) {
        worldsTable.put(world, isOn);
    }

    public HashMap<Location, String> getRadios() {
        return radios;
    }

    public void setRadios(HashMap<Location, String> radios) {
        this.radios = radios;
    }

    public YamlConfiguration getRadioYaml() {
        // Loads the "radios.yml" configuration.
        return loadConfigurationFromFile("radios.yml");
    }

    private YamlConfiguration getFrequencyYaml() {
        // Loads the "frequency.yml" configuration.
        return loadConfigurationFromFile("frequency.yml");
    }

    public String getFrequency(String name) {
        return getFrequencyYaml().getString(name);
    }

    public void addFrequency(Player player, String frequency) {
        // Gets the YamlConfiguration containing the frequencies.
        YamlConfiguration frequencyYaml = getFrequencyYaml();

        // Adds the frequency to the configuration.
        frequencyYaml.set(player.getName(), frequency);

        // Attempts to save the configuration to "frequency.yml"
        try {
            frequencyYaml.save(new File(getDataFolder(), "frequency.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save frequency.");
        } finally {
            player.sendMessage("Successfully set your frequency to " + ChatColor.YELLOW + frequency);
        }
    }

    // Configuration variable getter methods
    public int getPipboyID() {
        return pipboyID;
    }

    public double getScanChance() {
        return scanChance;
    }

    public int getIronBarExtension() {
        return ironBarExtension;
    }
}