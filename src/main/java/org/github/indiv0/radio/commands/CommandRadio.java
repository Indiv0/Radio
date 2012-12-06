package org.github.indiv0.radio.commands;

import org.bukkit.command.CommandSender;
import org.github.indiv0.radio.blah.Commands;

import ashulman.mbapi.commands.DelegatingCommand;
import ashulman.mbapi.util.ConfigurationContext;
import ashulman.typesafety.TypeSafeList;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(ConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 1);
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
