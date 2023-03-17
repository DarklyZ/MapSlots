package net.fabricmc.example.drawables;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MapSlotsWidget extends DrawableHelper implements Drawable {
    private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
    public static final int width = 166;
    public static final int height = 166;
    private boolean open = false;
    private int parentX;
    private int parentY;

    public boolean isOpen() { return this.open; }
    public void setOpen(boolean opened) { this.open = opened; }
    public void toggleOpen() { this.setOpen(!isOpen()); }

    public void initialize(int parentX, int parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }

    public int getMoveX(int parentX) {
        this.parentX = parentX + (isOpen() ? width / 2 : -width / 2);
        return this.parentX;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0.0F, 0.0F, 100.0F);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexture(matrices, this.parentX-2 - width, this.parentY, width, height, 0, 0, 64, 64, 64, 64);

        matrices.pop();
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
        return !isOpen() || mouseX < (double)left-2 - width || mouseY < (double)top || mouseX >= (double)left-2 || mouseY >= (double)top + height;
    }
}
