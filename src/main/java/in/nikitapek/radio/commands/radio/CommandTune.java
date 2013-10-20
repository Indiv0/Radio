package in.nikitapek.radio.commands.radio;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;
import in.nikitapek.radio.commands.CommandRadio.RadioCommands;
import in.nikitapek.radio.management.RadioInfoManager;
import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.util.RadioConfigurationContext;
import in.nikitapek.radio.util.RadioUtil;
import in.nikitapek.radio.util.ScaleInvariantBigDecimal;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTune extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandTune(RadioConfigurationContext configurationContext) {
        super(configurationContext, RadioCommands.TUNE, 1, 1);
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

        ScaleInvariantBigDecimal frequency = RadioUtil.getFrequencyFromString(args.get(0));

        if (frequency == null || Frequency.OFF.compareTo(frequency) >= 0) {
            player.sendMessage("Failed to set frequency. \"" + args.get(0) + "\" is an invalid frequency.");
            return false;
        }

        infoManager.setFrequency(player, frequency);
        player.sendMessage("Successfully set frequency to: " + ChatColor.YELLOW + frequency);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
