package de.craftery.hoppercounter;

import de.craftery.hoppercounter.command.SetDisplayCommand;
import de.craftery.hoppercounter.command.SetHopperCommand;
import de.craftery.hoppercounter.event.HopperListener;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class HopperCounter extends JavaPlugin {
    private static int itemsCollected = 0;
    private static Hologram hologram = null;
    private static HopperCounter instance;
    private final List<String> registeredCommands = new ArrayList<>();
    private HopperListener hopperListener;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.registerCommand("sethopper", new SetHopperCommand(this));
        this.registerCommand("setdisplay", new SetDisplayCommand(this));

        if (this.getConfig().contains("display.world")) {
            String world = this.getConfig().getString("display.world");
            if (world == null) return;

            setDisplay(new Location(
                            Bukkit.getWorld(world),
                            this.getConfig().getInt("display.x"),
                            this.getConfig().getInt("display.y"),
                            this.getConfig().getInt("display.z")
                    ),
                    this.getConfig().getInt("display.maxAmount")
            );
        }

        setItemsCollected(this.getConfig().getInt("itemsCollected"));

        hopperListener = new HopperListener(this);
    }

    private <T extends CommandExecutor & TabCompleter> void registerCommand(String command, T obj) {
        registeredCommands.add(command);
        PluginCommand pc = Bukkit.getPluginCommand(command);
        if (pc == null) return;
        pc.setExecutor(obj);
        pc.setTabCompleter(obj);
    }

    private void unregisterCommands() {
        for (String command : registeredCommands) {
            PluginCommand pc = Bukkit.getPluginCommand(command);
            if (pc == null) continue;
            pc.setExecutor(null);
            pc.setTabCompleter(null);
        }
    }

    public static void setDisplay(Location loc, int maxCount) {
        if (hologram != null) {
            hologram.getLines().clear();
            hologram.setPosition(loc);
        } else {
            HolographicDisplaysAPI api = HolographicDisplaysAPI.get(instance);
            hologram = api.createHologram(loc);
        }

        hologram.getLines().appendText("Challenge: " + itemsCollected + "/" + maxCount + " " + instance.getConfig().getString("hopper.material"));
    }

    private static void updateDisplay() {
        if (hologram != null) {
            hologram.getLines().clear();
            hologram.getLines().appendText("Challenge: " + itemsCollected + "/" + instance.getConfig().getInt("display.maxAmount") + " " + instance.getConfig().getString("hopper.material"));
        }
    }

    public static void setItemsCollected(int collected) {
        instance.getConfig().set("itemsCollected", collected);
        instance.saveConfig();
        itemsCollected = collected;
        updateDisplay();
    }

    @Override
    public void onDisable() {
        unregisterCommands();
        hopperListener = null;
    }
}
