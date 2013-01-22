package com.github.indiv0.radio.commands.radio;

import org.bukkit.command.CommandSender;

import com.github.indiv0.radio.management.RadioInfoManager;
import com.github.indiv0.radio.serialization.Frequency;
import com.github.indiv0.radio.util.Commands;
import com.github.indiv0.radio.util.RadioConfigurationContext;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.typesafety.TypeSafeCollections;
import ashulman.typesafety.TypeSafeList;

public class CommandOff extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandOff(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.OFF, 0, 0);
        infoManager = configurationContext.infoManager;
        pipboyId = configurationContext.pipboyId;
    }

    @Override
    protected boolean execute(final CommandSender sender, final TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (player.getItemInHand().getTypeId() != pipboyId) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return true;
        }

        infoManager.setFrequency(sender.getName(), Frequency.OFF);

        player.sendMessage("Successfully turned off the radio.");
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
