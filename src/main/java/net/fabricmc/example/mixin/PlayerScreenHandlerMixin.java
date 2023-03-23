package net.fabricmc.example.mixin;

import net.fabricmc.example.drawables.MapSlotsWidget;
import net.fabricmc.example.utils.MapSlotsHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> implements MapSlotsHandler {
    private final MapSlotsWidget mapSlotsWidget = new MapSlotsWidget();

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        this.addSlot(new Slot(mapSlotsWidget.inventory, 0, -mapSlotsWidget.getOutSide() - 20, 2) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
            public boolean canInsert(ItemStack stack) { return stack.isOf(Items.FILLED_MAP); }
        });
        this.addSlot(new Slot(mapSlotsWidget.inventory, 1, -mapSlotsWidget.getOutSide() - 20, 20) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
        });
        this.addSlot(new Slot(mapSlotsWidget.inventory, 2, -mapSlotsWidget.getOutSide() - 20, 38) {
            public boolean isEnabled() { return mapSlotsWidget.isOpen(); }
        });
    }

    public MapSlotsWidget getMSWidget() { return this.mapSlotsWidget; }
}
