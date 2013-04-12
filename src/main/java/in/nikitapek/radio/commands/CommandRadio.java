package in.nikitapek.radio.commands;

import in.nikitapek.radio.commands.radio.CommandOff;
import in.nikitapek.radio.commands.radio.CommandScan;
import in.nikitapek.radio.commands.radio.CommandTune;
import in.nikitapek.radio.util.Commands;
import in.nikitapek.radio.util.RadioConfigurationContext;

import com.amshulman.mbapi.commands.DelegatingCommand;

public class CommandRadio extends DelegatingCommand {

    public CommandRadio(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 2);
        registerSubcommand(new CommandTune(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
        registerSubcommand(new CommandOff(configurationContext));
    }
}
