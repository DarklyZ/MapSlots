package net.fabricmc.example.utils;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class MapSlot extends Slot {
    public MapSlot(int x, int y) {
        this(new MapSlotsInv(), 0, x, y);
    }

    public MapSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public boolean isEnabled() {
        return true;
    }
}
