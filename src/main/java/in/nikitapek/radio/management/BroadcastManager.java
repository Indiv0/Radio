package in.nikitapek.radio.management;

import in.nikitapek.radio.serialization.Frequency;
import in.nikitapek.radio.serialization.Radio;
import in.nikitapek.radio.util.RadioConfigurationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.javatuples.Pair;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.management.ChatManager;
import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeList;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.TypeSafeSet;
import com.amshulman.typesafety.impl.TypeSafeListImpl;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import com.amshulman.typesafety.impl.TypeSafeSetImpl;

public final class BroadcastManager implements Runnable {
    private final MbapiPlugin plugin;
    private final BukkitScheduler scheduler;
    private final RadioInfoManager infoManager;

    private final int radioReceiverId;
    private final double scanChance;

    public BroadcastManager(final RadioConfigurationContext configurationContext) {
        plugin = configurationContext.plugin;
        scheduler = Bukkit.getScheduler();
        infoManager = configurationContext.infoManager;
        radioReceiverId = configurationContext.pipboyId;
        scanChance = configurationContext.scanChance;
    }

    @Override
    public void run() {
        final TypeSafeSet<Player> scanningPlayers = infoManager.getListeners(Frequency.SCANNING);

        for (final Iterator<Radio> iter = infoManager.getRadios().iterator(); iter.hasNext();) {
            final Radio radio = iter.next();
            TypeSafeSet<Player> listeningPlayers = infoManager.getListeners(radio.getFrequency().getFrequency());

            if (listeningPlayers == null) {
                listeningPlayers = new TypeSafeSetImpl<>(new HashSet<Player>(), CoreTypes.PLAYER);
            }

            for (final Iterator<Player> iterPlayers = scanningPlayers.iterator(); iterPlayers.hasNext();) {
                final Player p = iterPlayers.next();
                if (Math.random() < scanChance) {
                    listeningPlayers.add(p);
                    iterPlayers.remove();
                }
            }

            if (listeningPlayers.isEmpty()) {
                continue;
            }

            Block block = radio.getBlock();
            if (!Material.JUKEBOX.equals(block.getType()) || !block.isBlockIndirectlyPowered()) {
                iter.remove();
                return;
            }

            final Location source = block.getLocation();
            final String[] message = Radio.getMessage(source).toArray(new String[0]);

            double innerRadius = 150;
            double outerRadius = 300;

            if (block.getWorld().hasStorm()) {
                if (block.getWorld().isThundering()) {
                    continue;
                }

                innerRadius /= 2;
                outerRadius /= 2;
            }

            final ChatColor color;
            block = block.getRelative(0, 1, 0);
            if (Material.WOOL.equals(block.getType())) {
                color = Radio.getChatColor(block);
            } else {
                color = ChatColor.GOLD;
                block = block.getRelative(0, -1, 0);
            }

            int ironBarCount;
            final TypeSafeMap<Player, Double> expanded = new TypeSafeMapImpl<>(new HashMap<Player, Double>(listeningPlayers.size()), CoreTypes.PLAYER, CoreTypes.DOUBLE);
            for (final Player player : listeningPlayers) {
                // Search the hotbar for the "pipboy" item to ensure the player can recieve signals.
                final int pipboyIndex = player.getInventory().first(radioReceiverId);

                // If the player does not have a "pipboy" in their hotbar, then the player cannot recieve the signal.
                if (pipboyIndex == -1 || pipboyIndex >= 9) {
                    expanded.put(player, 0d);
                    continue;
                }

                // Calculate the height of the iron block pillars in a diagonal around the player, and use them to modify the recieving power.
                ironBarCount = calculateIronBarsSurroundingPlayer(player, 1, 0, 1);
                ironBarCount = Math.min(ironBarCount, calculateIronBarsSurroundingPlayer(player, 1, 0, -1));
                ironBarCount = Math.min(ironBarCount, calculateIronBarsSurroundingPlayer(player, -1, 0, 1));
                ironBarCount = Math.min(ironBarCount, calculateIronBarsSurroundingPlayer(player, -1, 0, -1));

                expanded.put(player, Math.pow(1.01592540028, ironBarCount));
            }

            ironBarCount = 0;
            while (Material.IRON_FENCE.equals(block.getRelative(0, 1, 0).getType())) {
                block = block.getRelative(0, 1, 0);
                ++ironBarCount;
            }
            final double rangeExtension = Math.pow(1.02299172025d, ironBarCount);
            innerRadius *= rangeExtension;
            outerRadius *= rangeExtension;

            final String prefix = ChatColor.RED + "[Radio " + radio.getFrequency().getFrequency() + "] " + color;
            final TypeSafeMap<String, String[]> messages = ChatManager.reduce((int) innerRadius, (int) outerRadius, radio.getBroadcastClarity(), source, listeningPlayers, expanded, message);

            final TypeSafeList<Pair<String, String[]>> toSend = new TypeSafeListImpl<>(new ArrayList<Pair<String, String[]>>(), CoreTypes.MESSAGE_PAIR);
            for (final Entry<String, String[]> e : messages.entrySet()) {
                final String[] arr = e.getValue();

                for (int i = 0; i < arr.length; ++i) {
                    arr[i] = prefix + arr[i];
                }

                toSend.add(new Pair<>(e.getKey(), arr));
            }

            scheduler.runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    ChatManager.dispatchDifferentMessages(toSend);
                }
            });

        }
    }

    private static int calculateIronBarsSurroundingPlayer(final Player player, final int modX, final int modY, final int modZ) {
        // Gets the block at the requested offset.
        Block currentBlock = player.getLocation().getBlock().getRelative(modX, modY, modZ);

        // Checks to make sure that the block has IRON_FENCE blocks.
        if (player.getLocation().getBlock().getRelative(modX, modY, modZ).getType() != Material.IRON_FENCE) {
            return 0;
        }

        int ironBarCount;
        for (ironBarCount = 1; currentBlock.getRelative(0, 1, 0).getType() == Material.IRON_FENCE; ironBarCount++) {
            currentBlock = currentBlock.getRelative(0, 1, 0);
        }

        return ironBarCount;
    }
}
