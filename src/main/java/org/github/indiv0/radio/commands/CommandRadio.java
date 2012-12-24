package org.github.indiv0.radio.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.github.indiv0.radio.commands.radio.CommandScan;
import org.github.indiv0.radio.commands.radio.CommandTune;
import org.github.indiv0.radio.main.Commands;

import ashulman.mbapi.commands.DelegatingCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.mbapi.util.CoreTypes;
import ashulman.typesafety.TypeSafeCollections;
import ashulman.typesafety.TypeSafeList;
import ashulman.typesafety.impl.TypeSafeListImpl;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(final ConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 1);
        registerSubcommand(new CommandTune(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        if (args.size() != 1)
            return TypeSafeCollections.emptyList();

        final TypeSafeList<String> completions = new TypeSafeListImpl<String>(new ArrayList<String>(), CoreTypes.STRING);

        if (StringUtil.startsWithIgnoreCase("scan", args.get(0))) {
            completions.add("scan");
        } else if (StringUtil.startsWithIgnoreCase("tune", args.get(0))) {
            completions.add("tune");
        } else if (StringUtil.startsWithIgnoreCase("off", args.get(0))) {
            completions.add("off");
        }

        return completions;
    }
}
