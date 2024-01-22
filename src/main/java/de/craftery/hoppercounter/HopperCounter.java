package de.craftery.hoppercounter;

import de.craftery.hoppercounter.command.SetDisplayCommand;
import de.craftery.hoppercounter.command.SetHopperCommand;
import de.craftery.hoppercounter.event.HopperListener;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class HopperCounter extends JavaPlugin {
    private static int itemsCollected = 0;
    private static Hologram hologram = null;
    private static HopperCounter instance;

    @Override
    public void onEnable() {
        instance = this;
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        PluginCommand command = this.getCommand("sethopper");
        if (command != null) {
            SetHopperCommand commandInstance = new SetHopperCommand(this);
            command.setExecutor(commandInstance);
            command.setTabCompleter(commandInstance);
        }

        command = this.getCommand("setdisplay");
        if (command != null) {
            SetDisplayCommand commandInstance = new SetDisplayCommand(this);
            command.setExecutor(commandInstance);
            command.setTabCompleter(commandInstance);
        }
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

        new HopperListener(this);
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
        PluginCommand command = this.getCommand("sethopper");
        if (command != null) {
            command.setExecutor(null);
            command.setTabCompleter(null);
        }
    }
}
