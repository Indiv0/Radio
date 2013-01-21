package org.github.indiv0.radio.commands;

import org.github.indiv0.radio.commands.radio.CommandOff;
import org.github.indiv0.radio.commands.radio.CommandScan;
import org.github.indiv0.radio.commands.radio.CommandTune;
import org.github.indiv0.radio.util.Commands;
import org.github.indiv0.radio.util.RadioConfigurationContext;

import ashulman.mbapi.commands.DelegatingCommand;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 2);
        registerSubcommand(new CommandTune(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
        registerSubcommand(new CommandOff(configurationContext));
    }
}
