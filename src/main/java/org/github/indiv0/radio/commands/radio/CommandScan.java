package org.github.indiv0.radio.commands.radio;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.indiv0.radio.main.Commands;
import org.github.indiv0.radio.main.RadioBroadcast;
import org.github.indiv0.radio.main.RadioPlugin;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeList;

public class CommandScan extends PlayerOnlyCommand {
    private final RadioPlugin plugin;

    public CommandScan(ConfigurationContext configurationContext) {
        super(configurationContext, Commands.TUNE, 0, 0);
        plugin = (RadioPlugin) configurationContext.plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (!RadioBroadcast.isPlayerHoldingPipboy(player)) return true;

        plugin.addFrequency((Player) sender, "scan");

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
