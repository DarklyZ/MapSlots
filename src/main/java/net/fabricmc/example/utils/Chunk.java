package net.fabricmc.example.utils;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Chunk extends DrawableHelper {
    private static final int side = 30;
    private final int x, y, offX, offY, limX, limY, limSide;

    private Chunk(int limX, int limY, int limSide, int offX, int offY) {
        this.offX = offX;
        this.offY = offY;
        this.limX = limX;
        this.limY = limY;
        this.limSide = limSide;

        this.x = getPoint(limX, limSide, offX);
        this.y = getPoint(limY, limSide, offY);
    }

    public static Chunk ofOffset(int limX, int limY, int limSide, int offX, int offY) {
        return new Chunk(limX, limY, limSide, offX, offY);
    }
    public static Chunk ofMouse(int limX, int limY, int limSide, int mouseX, int mouseY) {
        return new Chunk(limX, limY, limSide, getOffset(limX, limSide, mouseX), getOffset(limY, limSide, mouseY));
    }

    private static int getCenter(int lim, int limSide) { return lim + limSide/2; }
    private static int getPoint(int lim, int limSide, int off) { return getCenter(lim, limSide) + off * side; }
    private static int getOffset(int lim, int limSide, int mouse) {
        int center = getCenter(lim, limSide);
        return (mouse - center) / side - (mouse < center ? 1 : 0);
    }

    private int getX1() {
        return this.limX < this.x ? Math.min(this.x, this.limX + this.limSide) : this.limX;
    }
    private int getY1() {
        return this.limY < this.y ? Math.min(this.y, this.limY + this.limSide) : this.limY;
    }
    private int getX2() {
        return this.limX < this.x + side ? Math.min(this.x + side, this.limX + this.limSide) : this.limX;
    }
    private int getY2() {
        return this.limY < this.y + side ? Math.min(this.y + side, this.limY + this.limSide) : this.limY;
    }

    public void drawSelection(MatrixStack matrices) {
        fill(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), Color.GREEN.getRGB());
    }
}
