package in.nikitapek.radio;

import in.nikitapek.radio.commands.CommandRadio;
import in.nikitapek.radio.events.RadioListener;
import in.nikitapek.radio.management.BroadcastManager;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.util.RadioConfigurationContext;

import org.bukkit.Bukkit;

import com.amshulman.mbapi.MbapiPlugin;

public final class RadioPlugin extends MbapiPlugin {
    private RadioInfoManager infoManager;

    @Override
    public void onEnable() {
        final RadioConfigurationContext configurationContext = new RadioConfigurationContext(this);
        infoManager = configurationContext.infoManager;

        registerEventHandler(new RadioListener(configurationContext));
        registerCommandExecutor(new CommandRadio(configurationContext));

        // Schedules a broadcast task to handle radio message broadcasting.
        final Runnable broadcast = new BroadcastManager(configurationContext);
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
