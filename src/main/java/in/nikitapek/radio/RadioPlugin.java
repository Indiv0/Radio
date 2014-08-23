package in.nikitapek.radio;

import com.amshulman.mbapi.MbapiPlugin;
import in.nikitapek.radio.commands.CommandRadio;
import in.nikitapek.radio.events.RadioListener;
import in.nikitapek.radio.management.BroadcastManager;
import in.nikitapek.radio.util.RadioConfigurationContext;
import org.bukkit.Bukkit;

public class RadioPlugin extends MbapiPlugin {

    @Override
    public void onEnable() {
        RadioConfigurationContext configurationContext = new RadioConfigurationContext(this);

        registerEventHandler(new RadioListener(configurationContext));
        registerCommandExecutor(new CommandRadio(configurationContext));

        // Schedules a broadcast task to handle radio message broadcasting.
        Runnable broadcast = new BroadcastManager(configurationContext);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, broadcast, 20L, configurationContext.transmitDelay);

        super.onEnable();
    }

}
