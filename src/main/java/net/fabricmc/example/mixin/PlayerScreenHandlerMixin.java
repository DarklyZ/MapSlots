package net.fabricmc.example.mixin;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.fabricmc.example.utils.MapSlotsHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> implements MapSlotsHandler {
    private final MapSlotsWidget mapSlotsWidget = this.initMapSlotsWidget();

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    public MapSlotsWidget getMSWidget() { return this.mapSlotsWidget; }

    private MapSlotsWidget initMapSlotsWidget() {
        MapSlotsWidget mapSlotsWidget = new MapSlotsWidget();

        this.addSlot(new Slot(mapSlotsWidget.inventory, 0, -MapSlotsWidget.width - 20, 2) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
        });
        this.addSlot(new Slot(mapSlotsWidget.inventory, 1, -MapSlotsWidget.width - 20, 20) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
        });
        this.addSlot(new Slot(mapSlotsWidget.inventory, 2, -MapSlotsWidget.width - 20, 38) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
        });

        return mapSlotsWidget;
    }
}
