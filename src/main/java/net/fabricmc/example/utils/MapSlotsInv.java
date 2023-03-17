package net.fabricmc.example.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class MapSlotsInv implements Inventory {
    private final DefaultedList<ItemStack> itemStacks =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    public int size() {
        return this.itemStacks.size();
    }

    public boolean isEmpty() {
        return this.itemStacks.isEmpty();
    }

    public ItemStack getStack(int slot) {
        return slot >= this.size() ? ItemStack.EMPTY : this.itemStacks.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.itemStacks, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.itemStacks, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        this.itemStacks.set(slot, stack);
    }

    public void markDirty() { }

    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    public void clear() {
        this.itemStacks.clear();
    }
}
