package org.github.indiv0.radio.commands.radio;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.github.indiv0.radio.main.Commands;
import org.github.indiv0.radio.main.RadioPlugin;
import org.github.indiv0.radio.util.RadioUtil;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeList;

public class CommandTune extends PlayerOnlyCommand {
    private final RadioPlugin plugin;

    public CommandTune(final ConfigurationContext configurationContext) {
        super(configurationContext, Commands.TUNE, 1, 1);
        plugin = (RadioPlugin) configurationContext.plugin;
    }

    @Override
    protected boolean execute(final CommandSender sender, final TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (!RadioUtil.playerIsHoldingPipboy(player))
            return true;

        final String frequencyArg = args.get(0).toString().toLowerCase();

        if (RadioUtil.getFrequencyFromString(frequencyArg) == null) {
            player.sendMessage("Failed to set frequency. Frequency cannot be null.");
            return false;
        }

        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.valueOf(frequencyArg));
        } catch (Exception e) {
            player.sendMessage("Failed to set frequency. \"" + frequencyArg
                    + "\" is an invalid frequency.");
            return false;
        }

        plugin.setFrequency(sender.getName(), frequency);
        player.sendMessage("Successfully set frequency to: " + ChatColor.YELLOW
                + frequency);

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
