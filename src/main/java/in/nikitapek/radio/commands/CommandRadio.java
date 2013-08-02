package in.nikitapek.radio.commands;

import in.nikitapek.radio.commands.radio.CommandOff;
import in.nikitapek.radio.commands.radio.CommandScan;
import in.nikitapek.radio.commands.radio.CommandTune;
import in.nikitapek.radio.util.Commands;
import in.nikitapek.radio.util.RadioConfigurationContext;

import com.amshulman.mbapi.commands.DelegatingCommand;
import com.amshulman.mbapi.util.PermissionsEnum;

public class CommandRadio extends DelegatingCommand {
    public CommandRadio(RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.RADIO, 1, 2);
        registerSubcommand(new CommandOff(configurationContext));
        registerSubcommand(new CommandScan(configurationContext));
        registerSubcommand(new CommandTune(configurationContext));
    }

    public enum RadioCommands implements PermissionsEnum {
        OFF, SCAN, TUNE;

        private static final String PREFIX;

        static {
            PREFIX = Commands.RADIO.getPrefix() + Commands.RADIO.name() + ".";
        }

        @Override
        public String getPrefix() {
            return PREFIX;
        }
    }
}
