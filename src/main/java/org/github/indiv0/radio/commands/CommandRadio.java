package org.github.indiv0.radio.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.github.indiv0.radio.commands.radio.CommandTune;
import org.github.indiv0.radio.main.Commands;

import ashulman.mbapi.commands.DelegatingCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.mbapi.util.CoreTypes;
import ashulman.typesafety.TypeSafeCollections;
import ashulman.typesafety.TypeSafeList;
import ashulman.typesafety.impl.TypeSafeListImpl;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(ConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 1);
        registerSubcommand(new CommandTune(configurationContext));
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        if (args.size() != 1) { return TypeSafeCollections.emptyList(); }

        TypeSafeList<String> completions = new TypeSafeListImpl<String>(CoreTypes.STRING);

        if (StringUtil.startsWithIgnoreCase("scan", args.get(0))) {
            completions.add("scan");
        } else if (StringUtil.startsWithIgnoreCase("tune", args.get(0))) {
            completions.add("tune");
        }

        return completions;
    }
}
