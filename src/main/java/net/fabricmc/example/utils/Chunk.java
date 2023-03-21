package net.fabricmc.example.utils;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Chunk extends DrawableHelper {
    private static final int side = 30;
    private final int offX, offY, limX, limY, limSide;

    private Chunk(Square square, int offX, int offY) {
        this.offX = offX;
        this.offY = offY;
        this.limX = square.getSqX();
        this.limY = square.getSqY();
        this.limSide = square.getSqSide();
    }

    public static Chunk ofOffset(Square square, int offX, int offY) {
        return new Chunk(square, offX, offY);
    }
    public static Chunk ofMouse(Square square, int mouseX, int mouseY) {
        int offX = getOffset(square.getSqX(), square.getSqSide(), mouseX);
        int offY = getOffset(square.getSqY(), square.getSqSide(), mouseY);
        return new Chunk(square, offX, offY);
    }

    private static int getCenter(int lim, int limSide) { return lim + limSide/2; }
    private static int getPoint(int lim, int limSide, int off) {
        return getCenter(lim, limSide) + off * side;
    }
    private static int getOffset(int lim, int limSide, int mouse) {
        int center = getCenter(lim, limSide);
        return (mouse - center) / side - (mouse < center ? 1 : 0);
    }

    private int getX1() {
        int x = getPoint(this.limX, this.limSide, this.offX);
        return this.limX < x ? Math.min(x, this.limX + this.limSide) : this.limX;
    }
    private int getY1() {
        int y = getPoint(this.limY, this.limSide, this.offY);
        return this.limY < y ? Math.min(y, this.limY + this.limSide) : this.limY;
    }
    private int getX2() {
        int x = getPoint(this.limX, this.limSide, this.offX) + side;
        return this.limX < x ? Math.min(x, this.limX + this.limSide) : this.limX;
    }
    private int getY2() {
        int y = getPoint(this.limY, this.limSide, this.offY) + side;
        return this.limY < y ? Math.min(y, this.limY + this.limSide) : this.limY;
    }

    public void drawSelection(MatrixStack matrices) {
        fill(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), Color.GREEN.getRGB());
    }
}
