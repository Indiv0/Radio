package in.nikitapek.radio.commands.radio;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;
import in.nikitapek.radio.commands.CommandRadio.RadioCommands;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.util.RadioConfigurationContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandScan extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandScan(RadioConfigurationContext configurationContext) {
        super(configurationContext, RadioCommands.SCAN, 0, 0);
        assert (configurationContext.infoManager != null);
        assert (configurationContext.pipboyId > 0);

        infoManager = configurationContext.infoManager;
        pipboyId = configurationContext.pipboyId;
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (player.getItemInHand().getTypeId() != pipboyId) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return true;
        }

        infoManager.setFrequency(player, Frequency.SCANNING);
        player.sendMessage("Successfully set radio to scan mode.");
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
