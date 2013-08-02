package in.nikitapek.radio.commands.radio;

import in.nikitapek.radio.commands.CommandRadio.RadioCommands;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.util.RadioConfigurationContext;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandOff extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandOff(RadioConfigurationContext configurationContext) {
        super(configurationContext, RadioCommands.OFF, 0, 0);
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

        infoManager.setFrequency(player, Frequency.OFF);
        player.sendMessage("Successfully turned off the radio.");
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
