package eu.diesesfloo.iui;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Accessors(chain = true)
public class ClickableItem implements Listener {

    static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @Getter
    @NonNull
    ItemStack item;

    @Setter
    UserInterface parent;

    @Setter
    Runnable onClick;

    @Setter
    Runnable onRightClick;

    @Setter
    Runnable onShiftClick;

    @Setter
    Runnable onShiftRightClick;

    @Setter
    Runnable onMiddleClick;

    @Setter
    Runnable onDrop;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void handleClick(InventoryClickEvent event) {
        if (!isRelevantEvent(event)) return;
        event.setCancelled(true);
        playClickSound((Player) event.getWhoClicked());
        handleClickType(event.getClick());
    }

    private boolean isRelevantEvent(InventoryClickEvent event) {
        return event.getCurrentItem() != null
                && event.getCurrentItem().isSimilar(item);
    }

    private void playClickSound(Player player) {
        if (parent != null && parent.getClickSound() != null) {
            player.playSound(
                    player.getLocation(),
                    parent.getClickSound(),
                    1.0f,
                    1.0f
            );
        }
    }

    private void handleClickType(ClickType clickType) {
        switch (clickType) {
            case LEFT:
                submitIfNotNull(onClick);
                break;
            case RIGHT:
                submitIfNotNull(onRightClick);
                break;
            case SHIFT_LEFT:
                submitIfNotNull(onShiftClick);
                break;
            case SHIFT_RIGHT:
                submitIfNotNull(onShiftRightClick);
                break;
            case MIDDLE:
                submitIfNotNull(onMiddleClick);
                break;
            case DROP:
                submitIfNotNull(onDrop);
                break;
            default:
                break;
        }
    }

    private void submitIfNotNull(Runnable runnable) {
        if (runnable != null) {
            EXECUTOR_SERVICE.submit(runnable);
        }
    }
}
