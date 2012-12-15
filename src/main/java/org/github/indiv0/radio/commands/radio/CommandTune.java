package org.github.indiv0.radio.commands.radio;

import org.bukkit.command.CommandSender;
import org.github.indiv0.radio.main.Commands;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.main.RadioPlugin;
import org.github.indiv0.radio.util.RadioUtil;
import org.github.indiv0.serialization.Frequency;

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

        String frequencyArg = args.get(0).toString().toLowerCase();

        if (frequencyArg.equals("off")) {
            plugin.setFrequency(sender.getName(), Frequency.OFF);
            return true;
        }

        if (!RadioUtil.isStringValidFrequency(frequencyArg)) return false;

        plugin.setFrequency(sender.getName(), RadioUtil.parseStringToFrequency(frequencyArg));

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
