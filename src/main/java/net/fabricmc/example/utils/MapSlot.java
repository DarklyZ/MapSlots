package net.fabricmc.example.utils;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class MapSlot extends Slot {
    private final MapSlotsWidget mapSlotsWidget;

    public MapSlot(MapSlotsWidget mapSlotsWidget, int offX, int offY) {
        this(new MapSlotsInv(), mapSlotsWidget, offX, offY);
    }

    public MapSlot(Inventory inventory, MapSlotsWidget mapSlotsWidget, int offX, int offY) {
        super(inventory, inventory.size()-1, (-MapSlotsWidget.width/2-10) + (offX*18), (MapSlotsWidget.height/2-8) + (offY*18));
        this.mapSlotsWidget = mapSlotsWidget;
    }

    public boolean isEnabled() {
        return this.mapSlotsWidget.isOpen();
    }
}
