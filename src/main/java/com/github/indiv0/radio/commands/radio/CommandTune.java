package com.github.indiv0.radio.commands.radio;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ashulman.mbapi.commands.PlayerOnlyCommand;
import ashulman.typesafety.TypeSafeCollections;
import ashulman.typesafety.TypeSafeList;

import com.github.indiv0.radio.management.RadioInfoManager;
import com.github.indiv0.radio.util.Commands;
import com.github.indiv0.radio.util.RadioConfigurationContext;
import com.github.indiv0.radio.util.RadioUtil;

public class CommandTune extends PlayerOnlyCommand {
    private final RadioInfoManager infoManager;
    private final int pipboyId;

    public CommandTune(final RadioConfigurationContext configurationContext) {
        super(configurationContext, Commands.TUNE, 1, 1);
        infoManager = configurationContext.infoManager;
        pipboyId = configurationContext.pipboyId;
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        // Makes sure that the currently held item is the "Pipboy".
        if (player.getItemInHand().getTypeId() != pipboyId) {
            player.sendMessage("You must be holding a compass to work the radio.");
            return true;
        }

        final String frequencyArg = args.get(0).toString().toLowerCase();

        if (RadioUtil.getFrequencyFromString(frequencyArg) == null) {
            player.sendMessage("Failed to set frequency. Frequency cannot be null.");
            return false;
        }

        BigDecimal frequency;

        try {
            frequency = BigDecimal.valueOf(Double.valueOf(frequencyArg));
        }
        catch (Exception e) {
            player.sendMessage("Failed to set frequency. \"" + frequencyArg + "\" is an invalid frequency.");
            return false;
        }

        infoManager.setFrequency(player, frequency);
        player.sendMessage("Successfully set frequency to: " + ChatColor.YELLOW + frequency);
        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
