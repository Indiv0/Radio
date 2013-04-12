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
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
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

public class BroadcastManager implements Runnable {

    private final MbapiPlugin plugin;
    private final BukkitScheduler scheduler;
    private final RadioInfoManager infoManager;

    private final int radioRecieverId;

    private double scanChance = 0.05d;

    public BroadcastManager(RadioConfigurationContext configurationContext) {
        plugin = configurationContext.plugin;
        scheduler = Bukkit.getScheduler();
        infoManager = configurationContext.infoManager;
        radioRecieverId = configurationContext.pipboyId;
    }

    @Override
    public void run() {
        TypeSafeSet<Player> scanningPlayers = infoManager.getListeners(Frequency.SCANNING);

        for (Iterator<Radio> iter = infoManager.getRadios().iterator(); iter.hasNext();) {
            Radio radio = iter.next();
            TypeSafeSet<Player> listeningPlayers = infoManager.getListeners(radio.getFrequency().getFrequency());

            if (listeningPlayers == null) {
                listeningPlayers = new TypeSafeSetImpl<Player>(new HashSet<Player>(), CoreTypes.PLAYER);
            }

            for (Iterator<Player> iterPlayers = scanningPlayers.iterator(); iterPlayers.hasNext();) {
                Player p = iterPlayers.next();
                if (Math.random() < scanChance) {
                    listeningPlayers.add(p);
                    iter.remove();
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

            Location source = block.getLocation();
            String[] message = Radio.getMessage(source).toArray(new String[0]);

            double innerRadius = 150;
            double outerRadius = 300;

            if (block.getWorld().hasStorm()) {
                if (block.getWorld().isThundering()) {
                    continue;
                }

                innerRadius /= 2;
                outerRadius /= 2;
            }

            block = block.getRelative(0, 1, 0);
            double broadcastClarity;
            switch (block.getType()) {
                case LAPIS_BLOCK:
                    broadcastClarity = 0.2;
                    break;
                case IRON_BLOCK:
                    broadcastClarity = 0.4;
                    break;
                case GOLD_BLOCK:
                    broadcastClarity = 0.65;
                    break;
                case DIAMOND_BLOCK:
                    broadcastClarity = 1.0;
                    break;
                default:
                    broadcastClarity = 0.1;
                    block = block.getRelative(0, -1, 0);
                    break;
            }

            ChatColor color = ChatColor.GOLD;
            block = block.getRelative(0, 1, 0);
            if (Material.WOOL.equals(block.getType())) {
                DyeColor dyeColor = ((Wool) block.getState().getData()).getColor();
                switch (dyeColor) {
                    case BLACK:
                        color = ChatColor.BLACK;
                        break;
                    case BLUE:
                        color = ChatColor.BLUE;
                        break;
                    case LIGHT_BLUE:
                        color = ChatColor.AQUA;
                        break;
                    case GRAY:
                        color = ChatColor.GRAY;
                        break;
                    case GREEN:
                        color = ChatColor.GREEN;
                        break;
                    case MAGENTA:
                        color = ChatColor.LIGHT_PURPLE;
                        break;
                    case PURPLE:
                        color = ChatColor.DARK_PURPLE;
                        break;
                    case RED:
                        color = ChatColor.RED;
                        break;
                    case WHITE:
                        color = ChatColor.WHITE;
                        break;
                    case YELLOW:
                        color = ChatColor.YELLOW;
                        break;
                    default:
                        color = ChatColor.MAGIC;
                        break;
                }
            } else {
                block = block.getRelative(0, -1, 0);
            }

            int ironBarCount = 0;
            while (Material.IRON_FENCE.equals(block.getRelative(0, 1, 0).getType())) {
                block = block.getRelative(0, 1, 0);
                ironBarCount++;
            }

            TypeSafeMap<Player, Double> expanded = new TypeSafeMapImpl<Player, Double>(new HashMap<Player, Double>(listeningPlayers.size()), CoreTypes.PLAYER, CoreTypes.DOUBLE);
            for (Player player : listeningPlayers) {
                if (player.getItemInHand().getTypeId() == radioRecieverId) {
                    ironBarCount = 0;
                    ironBarCount += calculateIronBarsSurroundingPlayer(player, 1, 0, 1);
                    ironBarCount += calculateIronBarsSurroundingPlayer(player, 1, 0, -1);
                    ironBarCount += calculateIronBarsSurroundingPlayer(player, -1, 0, 1);
                    ironBarCount += calculateIronBarsSurroundingPlayer(player, -1, 0, -1);

                    expanded.put(player, Math.pow(1.02299172025d, ironBarCount));
                } else {
                    expanded.put(player, 0d);
                }
            }

            String prefix = ChatColor.RED + "[Radio " + radio.getFrequency().getFrequency() + "] " + color;
            TypeSafeMap<String, String[]> messages = ChatManager.reduce((int) innerRadius, (int) outerRadius, broadcastClarity, source, listeningPlayers, expanded, message);

            final TypeSafeList<Pair<String, String[]>> toSend = new TypeSafeListImpl<Pair<String, String[]>>(new ArrayList<Pair<String, String[]>>(), CoreTypes.MESSAGE_PAIR);
            for (Entry<String, String[]> e : messages.entrySet()) {
                String[] arr = e.getValue();

                for (int i = 0; i < arr.length; ++i) {
                    arr[i] = prefix + arr[i];
                }

                toSend.add(new Pair<String, String[]>(e.getKey(), arr));
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
