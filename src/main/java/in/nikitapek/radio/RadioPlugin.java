package in.nikitapek.radio;

import in.nikitapek.radio.commands.CommandRadio;
import in.nikitapek.radio.events.RadioListener;
import in.nikitapek.radio.management.BroadcastManager;
import in.nikitapek.radio.util.RadioConfigurationContext;

import org.bukkit.Bukkit;

import com.amshulman.mbapi.MbapiPlugin;

public class RadioPlugin extends MbapiPlugin {

    @Override
    public void onEnable() {
        RadioConfigurationContext configurationContext = new RadioConfigurationContext(this);

        registerEventHandler(new RadioListener(configurationContext));
        registerCommandExecutor(new CommandRadio(configurationContext));

        // Schedules a broadcast task to handle radio message broadcasting.
        Runnable broadcast = new BroadcastManager(configurationContext);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L, 100L);

        super.onEnable();
    }

}
