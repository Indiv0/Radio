package org.github.indiv0.radio;

import org.bukkit.Bukkit;
import org.github.indiv0.radio.commands.CommandRadio;
import org.github.indiv0.radio.events.RadioListener;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.management.RadioInfoManager;
import org.github.indiv0.radio.util.RadioConfigurationContext;

import ashulman.mbapi.plugin.MbapiPlugin;

public class RadioPlugin extends MbapiPlugin {

    private RadioInfoManager infoManager;

    @Override
    public void onEnable() {
        // Initializes the configurationContext.
        RadioConfigurationContext configurationContext = new RadioConfigurationContext(this);
        // Initializes the infoManager.
        infoManager = configurationContext.infoManager;

        // Registers the event handler and the command executor.
        registerEventHandler(new RadioListener(configurationContext));
        registerCommandExecutor("radio", new CommandRadio(configurationContext));

        // Schedules a broadcast task to handle radio message broadcasting.
        RadioBroadcast broadcast = new RadioBroadcast(configurationContext);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L, 100L);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (infoManager != null) {
            infoManager.unloadAll();
        }
    }
}
