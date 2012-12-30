package org.github.indiv0.radio.commands.radio;

import org.bukkit.command.CommandSender;
import org.github.indiv0.radio.main.Commands;
import org.github.indiv0.radio.main.RadioPlugin;
import org.github.indiv0.radio.serialization.Frequency;
import org.github.indiv0.radio.util.RadioUtil;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeList;

public class CommandOff extends PlayerOnlyCommand {
    private final RadioPlugin plugin;

    public CommandOff(final ConfigurationContext configurationContext) {
        super(configurationContext, Commands.OFF, 0, 0);
        plugin = (RadioPlugin) configurationContext.plugin;
    }

    @Override
    protected boolean execute(final CommandSender sender, final TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (!RadioUtil.playerIsHoldingPipboy(player))
            return true;

        plugin.setFrequency(sender.getName(), Frequency.OFF);

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
