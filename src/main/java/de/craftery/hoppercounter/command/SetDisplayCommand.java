package de.craftery.hoppercounter.command;

import de.craftery.hoppercounter.HopperCounter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetDisplayCommand implements CommandExecutor, TabCompleter {
    private final HopperCounter plugin;

    public SetDisplayCommand (HopperCounter counter) {
        this.plugin = counter;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Dieser Befehl kann nur von Spielern ausgeführt werden").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Du musst die maximale Anzahl an zu zählenden Blöcken angeben").color(NamedTextColor.RED));
            return true;
        }

        int maxAmount;
        try {
            maxAmount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Du musst eine Zahl angeben").color(NamedTextColor.RED));
            return true;
        }

        if (maxAmount < 1) {
            sender.sendMessage(Component.text("Die Zahl muss größer als 0 sein").color(NamedTextColor.RED));
            return true;
        }

        Location targetLoc = player.getLocation();
        this.plugin.getConfig().set("display.x", targetLoc.getBlockX());
        this.plugin.getConfig().set("display.y", targetLoc.getBlockY());
        this.plugin.getConfig().set("display.z", targetLoc.getBlockZ());
        this.plugin.getConfig().set("display.world", targetLoc.getWorld().getName());
        this.plugin.getConfig().set("display.maxAmount", maxAmount);
        this.plugin.saveConfig();

        HopperCounter.setDisplay(targetLoc, maxAmount);

        sender.sendMessage(Component.text("Die Anzeige wurde gesetzt").color(NamedTextColor.GREEN));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
