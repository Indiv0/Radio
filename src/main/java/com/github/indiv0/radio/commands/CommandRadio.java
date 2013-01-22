package com.github.indiv0.radio.commands;

import org.bukkit.command.CommandSender;

import com.github.indiv0.radio.commands.radio.CommandOff;
import com.github.indiv0.radio.commands.radio.CommandScan;
import com.github.indiv0.radio.commands.radio.CommandTune;
import com.github.indiv0.radio.util.Commands;
import com.github.indiv0.radio.util.RadioConfigurationContext;

import ashulman.mbapi.commands.DelegatingCommand;
import ashulman.typesafety.TypeSafeList;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 2);
        registerSubcommand(new CommandTune(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
        registerSubcommand(new CommandOff(configurationContext));
    }

    @Override
    public TypeSafeList<String> onTabComplete(CommandSender sender, TypeSafeList<String> args) {
        // TODO Auto-generated method stub
        return null;
    }
}
