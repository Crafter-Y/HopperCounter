package de.craftery.hoppercounter.command;

import de.craftery.hoppercounter.HopperCounter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetHopperCommand implements CommandExecutor, TabCompleter {
    private final HopperCounter plugin;

    public SetHopperCommand(HopperCounter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Dieser Befehl kann nur von Spielern ausgeführt werden").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Du musst den zu zählenden Block angeben").color(NamedTextColor.RED));
            return true;
        }

        Material material = Material.getMaterial(args[0].toUpperCase());
        if (material == null) {
            sender.sendMessage(Component.text("Dieser Block existiert nicht").color(NamedTextColor.RED));
            return true;
        }

        Block target = player.getTargetBlock(null, 5);

        if (target.getType() != Material.HOPPER) {
            sender.sendMessage(Component.text("Du musst auf einen Trichter schauen").color(NamedTextColor.RED));
            return true;
        }

        Location targetLoc = target.getLocation();
        this.plugin.getConfig().set("hopper.x", targetLoc.getBlockX());
        this.plugin.getConfig().set("hopper.y", targetLoc.getBlockY());
        this.plugin.getConfig().set("hopper.z", targetLoc.getBlockZ());
        this.plugin.getConfig().set("hopper.world", targetLoc.getWorld().getName());
        this.plugin.getConfig().set("hopper.material", material.toString());
        this.plugin.saveConfig();

        player.sendMessage(Component.text("Der Trichter wurde erfolgreich gesetzt").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return Arrays.stream(Material.values())
                .map(Enum::toString)
                .filter(material -> material.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();

        return new ArrayList<>();
    }
}
