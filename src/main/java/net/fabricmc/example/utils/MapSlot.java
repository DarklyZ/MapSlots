package net.fabricmc.example.utils;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class MapSlot extends Slot {
    public MapSlot() {
        this(new MapSlotsInv(), 0, -MapSlotsWidget.getWidth()/2-10, MapSlotsWidget.getHeight()/2-8);
    }

    public MapSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public boolean isEnabled() {
        return true;
    }
}
