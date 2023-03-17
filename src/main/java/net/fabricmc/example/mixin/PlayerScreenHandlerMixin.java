package net.fabricmc.example.mixin;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.fabricmc.example.utils.MapSlot;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {
    private final MapSlot mapSlot = (MapSlot)this.addSlot(new MapSlot(
            -MapSlotsWidget.getWidth()/2-10, MapSlotsWidget.getHeight()/2-8
    ));

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }
}
