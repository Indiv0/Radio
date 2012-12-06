package org.github.indiv0.radio.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.github.indiv0.radio.blah.Commands;
import org.github.indiv0.radio.blah.Radio;
import org.github.indiv0.radio.blah.RadioPlugin;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.mbapi.util.CoreTypes;
import ashulman.typesafety.TypeSafeCollections;
import ashulman.typesafety.TypeSafeList;
import ashulman.typesafety.impl.TypeSafeListImpl;

public class CommandRadio extends PlayerOnlyCommand {
    private final RadioPlugin plugin;

    public CommandRadio(ConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 1);
        plugin = (RadioPlugin) configurationContext.plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy" (by default
        // the compass).
        if (player.getItemInHand().getTypeId() != plugin.getPipboyID()) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return true;
        }

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
        if (args.size() != 1) { return TypeSafeCollections.emptyList(); }

        TypeSafeList<String> completions = new TypeSafeListImpl<String>(CoreTypes.STRING);

        if (StringUtil.startsWithIgnoreCase("scan", args.get(0))) {
            completions.add("scan");
        }

        return completions;
    }
}
