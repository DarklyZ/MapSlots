package net.fabricmc.example.drawables;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.utils.Chunk;
import net.fabricmc.example.utils.Square;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.Identifier;

public class MapSlotsWidget extends DrawableHelper implements Drawable, Square {
    private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
    private static final int side = 166;
    public final Inventory inventory = new SimpleInventory(3);
    private boolean open = false;
    private int parentX;
    private int parentY;

    public boolean isOpen() { return this.open; }
    public void setOpen(boolean opened) { this.open = opened; }
    public void toggleOpen() { this.setOpen(!isOpen()); }

    public boolean isChangeMode() {
        return !this.inventory.getStack(0).isEmpty();
    }

    public Chunk getChunk(int mouseX, int mouseY) {
        return Chunk.ofMouse(this, mouseX, mouseY);
    }

    public void initialize(int parentX, int parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }
    
    public int getMoveX(int parentX) {
        this.parentX = parentX + (isOpen() ? side / 2 : -side / 2);
        return this.parentX;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0.0F, 0.0F, 100.0F);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexture(matrices, this.getSqX(), this.getSqY(), side, side, 0, 0, 64, 64, 64, 64);

        if (this.isChangeMode())
            this.getChunk(mouseX, mouseY).drawSelection(matrices);

        matrices.pop();
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
        return !isOpen() || mouseX < (double)left-20 - side || mouseY < (double)top || mouseX >= (double)left-2 || mouseY >= (double)top + side;
    }

    public int getSqX() { return this.parentX-2 - side; }
    public int getSqY() { return this.parentY; }
    public int getSqSide() { return side; }
}
