package net.fabricmc.example.mixin;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.fabricmc.example.utils.MapSlot;
import net.fabricmc.example.utils.MapSlotsHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> implements MapSlotsHandler {
    private final MapSlotsWidget mapSlotsWidget = new MapSlotsWidget();
    private final MapSlot mapSlot = (MapSlot)this.addSlot(new MapSlot(this.mapSlotsWidget, 0, 0));

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    public MapSlotsWidget getMSWidget() { return this.mapSlotsWidget; }
}
