package org.github.indiv0.radio.commands.radio;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.indiv0.radio.main.Commands;
import org.github.indiv0.radio.main.Radio;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.main.RadioPlugin;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeList;

public class CommandTune extends PlayerOnlyCommand {
    private final RadioPlugin plugin;

    public CommandTune(ConfigurationContext configurationContext) {
        super(configurationContext, Commands.TUNE, 1, 1);
        plugin = (RadioPlugin) configurationContext.plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (!RadioBroadcast.isPlayerHoldingPipboy(player)) return true;

        String frequencyArg = args.get(0).toString();

        // This simply sets the frequency to "scan" which simply searches for
        // all messages.
        if (frequencyArg.equalsIgnoreCase("scan") ||
                Radio.convertFrequencyToIntegerNotation(frequencyArg) != null) {
            plugin.addFrequency((Player) sender, frequencyArg);
            return true;
        }

        return false;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
