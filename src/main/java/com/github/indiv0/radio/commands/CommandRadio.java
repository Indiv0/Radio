package com.github.indiv0.radio.commands;

import ashulman.mbapi.commands.DelegatingCommand;

import com.github.indiv0.radio.commands.radio.CommandOff;
import com.github.indiv0.radio.commands.radio.CommandScan;
import com.github.indiv0.radio.commands.radio.CommandTune;
import com.github.indiv0.radio.util.Commands;
import com.github.indiv0.radio.util.RadioConfigurationContext;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 2);
        registerSubcommand(new CommandTune(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
        registerSubcommand(new CommandOff(configurationContext));
    }
}
