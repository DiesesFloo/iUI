package eu.diesesfloo.iui;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public record UserInterfaceHolder(@NonNull Player player,
                                  @NonNull UserInterface userInterface) implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return userInterface.createInventoryFor(player);
    }
}
