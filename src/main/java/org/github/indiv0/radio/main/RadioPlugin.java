package org.github.indiv0.radio.main;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.github.indiv0.radio.commands.CommandRadio;
import org.github.indiv0.radio.events.RadioBlockListener;
import org.github.indiv0.radio.events.RadioPlayerListener;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.serialization.Radio;
import org.github.indiv0.radio.storage.InfoManager;

import ashulman.mbapi.plugin.MbapiPlugin;
import ashulman.mbapi.storage.TypeSafeStorageSet;
import ashulman.mbapi.util.ConfigurationContext;

public class RadioPlugin extends MbapiPlugin {
    private final RadioBroadcast broadcast = new RadioBroadcast(this);

    private final Map<World, Boolean> worldsTable = new HashMap<World, Boolean>();
    private int pipboyID = 345;
    private double scanChance = 0.01;
    private int ironBarExtension = 30;

    private ConfigurationContext configurationContext;
    private InfoManager infoManager;

    @Override
    public void onEnable() {
        // Initializes the configurationContext.
        configurationContext = new ConfigurationContext(this);
        // Initializes the infoManager.
        infoManager = new InfoManager(configurationContext);

        // Loads the configuration file.
        if (!loadConfig()) {
            getLogger().log(Level.WARNING, this
                    + " has encountered an error while reading the configuration file, continuing with defaults.");
        }

        // Registers the two event handlers and the command executor.
        registerEventHandler(new RadioPlayerListener(this));
        registerEventHandler(new RadioBlockListener(this));
        registerCommandExecutor("radio", new CommandRadio(configurationContext));

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
        // Tries to load the configuration from the file into configYaml.
        YamlConfiguration configYaml = (YamlConfiguration) getConfig();

        // Retrieves the worlds in which frequency scanning is enabled.
        for (final World world : getServer().getWorlds()) {
            setOn(world, configYaml.getBoolean("worlds." + world.getName(), false));
        }

        // Retrieves the "pipboy" item ID.
        pipboyID = configYaml.getInt("pipboyID", getPipboyID());
        // Retrieves the chance for a player on the "scan" frequency to receive
        // a broadcast.
        scanChance = configYaml.getDouble("scanChance", getScanChance());
        // Retrieves the chance for a player on the "scan" frequency to receive
        // a broadcast.
        ironBarExtension = configYaml.getInt("ironBarExtension", getIronBarExtension());

        return true;
    }

    public void addRadio(final Radio radio) {
        if (getRadioByLocation(radio.getLocation()) != null) {
            removeRadio(getRadioByLocation(radio.getLocation()));
        }

        getRadios().add(radio);
    }

    public void removeRadio(final Radio radio) {
        // Cancels any tasks scheduled by this plugin.
        // getServer().getScheduler().cancelTasks(this);

        // Removes the radio.
        getRadios().remove(radio);

        // Schedules a broadcast task to handle radio message broadcasting.
        // Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L,
        // 100L);
    }

    // Getter and Setter methods
    public Map<World, Boolean> getOn() {
        return worldsTable;
    }

    public void setOn(final World world, final boolean isOn) {
        worldsTable.put(world, isOn);
    }

    public TypeSafeStorageSet<Radio> getRadios() {
        return infoManager.getRadios();
    }

    public Radio getRadioByLocation(final Location location) {
        for (final Radio radio : getRadios())
            if (radio.getLocation().equals(location))
                return radio;

        return null;
    }

    public Frequency getFrequency(final String name) {
        return infoManager.getFrequency(name);
    }

    public void setFrequency(final String playerName, final BigDecimal frequency) {
        infoManager.setFrequency(playerName, frequency);
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