package in.nikitapek.radio.commands.radio;

import in.nikitapek.radio.commands.CommandRadio.RadioCommands;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.util.RadioConfigurationContext;
import in.nikitapek.radio.util.RadioUtil;
import in.nikitapek.radio.util.ScaleInvariantBigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public final class CommandTune extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandTune(final RadioConfigurationContext configurationContext) {
        super(configurationContext, RadioCommands.TUNE, 1, 1);
        infoManager = configurationContext.infoManager;
        pipboyId = configurationContext.pipboyId;
    }

    @Override
    protected boolean executeForPlayer(final Player player, final TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (player.getItemInHand().getTypeId() != pipboyId) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return true;
        }

        final ScaleInvariantBigDecimal frequency = RadioUtil.getFrequencyFromString(args.get(0));

        if (frequency == null || Frequency.OFF.compareTo(frequency) >= 0) {
            player.sendMessage("Failed to set frequency. \"" + args.get(0) + "\" is an invalid frequency.");
            return false;
        }

        infoManager.setFrequency(player, frequency);
        player.sendMessage("Successfully set frequency to: " + ChatColor.YELLOW + frequency);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
