package eu.diesesfloo.iui;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class UserInterface implements Listener {

    static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @NonNull
    final JavaPlugin plugin;

    @Getter
    @Setter
    @NonNull
    Integer lines = 3;


    @Setter
    Sound openSound = null;

    @Getter
    @Setter
    Sound clickSound = null;

    @Setter
    ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    @Getter
    @Setter
    @NonNull
    TextComponent title = Component.text("GUI");

    @Setter
    Consumer<Player> onClose = null;

    @Setter
    boolean closeable = true;

    final Map<Integer, ClickableItem> items = new HashMap<>();

    public UserInterface(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer()
                .getPluginManager()
                .registerEvents(this, plugin);
    }

    public UserInterface setItem(int slot, @NonNull ClickableItem item) {
        if (slot < 0 || slot >= lines * 9) {
            throw new IllegalArgumentException("Slot must be between 0 and " + (lines * 9 - 1));
        }

        items.put(slot, item.setParent(this));
        registerItemListener(item);
        return this;
    }

    public UserInterface removeItem(int slot) {
        items.remove(slot);
        return this;
    }

    public Inventory createInventoryFor(Player player) {
        Inventory inventory = plugin.getServer()
                .createInventory(
                        new UserInterfaceHolder(player, this),
                        lines * 9,
                        title
                );
        for (int i = 0; i < lines * 9; i++) {
            inventory.setItem(i, background);
        }
        items.forEach((slot, item) -> inventory.setItem(slot, item.getItem()));
        return inventory;
    }

    public void open(Player player) {
        EXECUTOR_SERVICE.submit(() -> {
            playOpenSound(player);
            plugin.getServer().getScheduler().runTask(plugin, () -> player.openInventory(createInventoryFor(player)));
        });
    }

    private void playOpenSound(Player player) {
        if (openSound == null) return;
        player.playSound(player.getLocation(), openSound, 1.0f, 1.0f);
    }

    private void registerItemListener(ClickableItem item) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(item, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof UserInterfaceHolder holder)) return;
        if (!holder.userInterface().equals(this)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof UserInterfaceHolder holder)) return;
        if (!holder.userInterface().equals(this)) return;
        Player player = holder.player();
        if (closeable) {
            if (onClose == null)  return;
            EXECUTOR_SERVICE.submit(() -> onClose.accept(player));
            return;
        }
        
        plugin.getServer().getScheduler().runTask(plugin, () -> open(player));
    }

}
