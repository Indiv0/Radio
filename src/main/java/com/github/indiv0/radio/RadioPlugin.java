package com.github.indiv0.radio;

import org.bukkit.Bukkit;

import com.amshulman.mbapi.MbapiPlugin;
import com.github.indiv0.radio.commands.CommandRadio;
import com.github.indiv0.radio.events.RadioListener;
import com.github.indiv0.radio.management.BroadcastManager;
import com.github.indiv0.radio.management.RadioInfoManager;
import com.github.indiv0.radio.util.RadioConfigurationContext;

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
        registerCommandExecutor(new CommandRadio(configurationContext));

        // Schedules a broadcast task to handle radio message broadcasting.
        Runnable broadcast = new BroadcastManager(configurationContext);// new RadioBroadcast(configurationContext);
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
