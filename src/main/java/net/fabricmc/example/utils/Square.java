package net.fabricmc.example.utils;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;

public class Square extends DrawableHelper {
    public static final int side = 30;
    private final int x, y, offX, offY;

    public Square(int centerX, int centerY, int mouseX, int mouseY) {
        this.offX = (mouseX - centerX) / side - (mouseX < centerX ? 1 : 0);
        this.offY = (mouseY - centerY) / side - (mouseY < centerY ? 1 : 0);
        this.x = centerX + this.offX * side;
        this.y = centerY + this.offY * side;
    }

    public int getX1() { return this.x; }
    public int getY1() { return this.y; }
    public int getX2() { return this.x + side; }
    public int getY2() { return this.y + side; }

    public void drawSelection(MatrixStack matrices) {
        fill(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), Color.GREEN.getRGB());
    }
}
