package net.fabricmc.example.utils;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Chunk extends DrawableHelper {
    public static final int side = 30;
    private final int x, y, offX, offY;
    private int limX = 0, limY = 0, limSide = 0;

    public Chunk(int limX, int limY, int limSide, int mouseX, int mouseY) {
        int centerX = limX + limSide/2;
        int centerY = limY + limSide/2;

        this.limX = limX;
        this.limY = limY;
        this.limSide = limSide;

        this.offX = (mouseX - centerX) / side - (mouseX < centerX ? 1 : 0);
        this.offY = (mouseY - centerY) / side - (mouseY < centerY ? 1 : 0);
        this.x = centerX + this.offX * side;
        this.y = centerY + this.offY * side;
    }

    public int getX1() {
        return this.limX < this.x ? Math.min(this.x, this.limX + this.limSide) : this.limX;
    }
    public int getY1() {
        return this.limY < this.y ? Math.min(this.y, this.limY + this.limSide) : this.limY;
    }
    public int getX2() {
        return this.limX < this.x + side ? Math.min(this.x + side, this.limX + this.limSide) : this.limX;
    }
    public int getY2() {
        return this.limY < this.y + side ? Math.min(this.y + side, this.limY + this.limSide) : this.limY;
    }

    public void drawSelection(MatrixStack matrices) {
        fill(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), Color.GREEN.getRGB());
    }
}
