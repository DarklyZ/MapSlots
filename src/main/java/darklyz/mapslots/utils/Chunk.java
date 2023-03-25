package darklyz.mapslots.utils;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;

public class Chunk extends DrawableHelper {
    private static final int side = 30;
    private final int offX, offY;
    private final Region region;

    private Chunk(Region region, int offX, int offY) {
        this.offX = offX;
        this.offY = offY;
        this.region = region;
    }

    public static Chunk ofOffset(Region region, int offX, int offY) {
        return new Chunk(region, offX, offY);
    }
    public static Chunk ofMouse(Region region, int mouseX, int mouseY) {
        int offX = getOffset(region.getInX(), region.getInSide(), region.getOffX(), mouseX);
        int offY = getOffset(region.getInY(), region.getInSide(), region.getOffY(), mouseY);
        return new Chunk(region, offX, offY);
    }

    private static int getCenter(int lim, int limSide, int off) { return lim + limSide/2 + off; }
    private static int getPoint(int lim, int limSide, int gOff, int off) {
        return getCenter(lim, limSide, gOff) + off * side;
    }
    private static int getOffset(int lim, int limSide, int gOff, int mouse) {
        int center = getCenter(lim, limSide, gOff);
        return (mouse - center) / side - (mouse < center ? 1 : 0);
    }

    private int getX1() {
        int x = getPoint(this.region.getInX(), this.region.getInSide(), this.region.getOffX(),  this.offX);
        return this.region.getInX() < x ? Math.min(x, this.region.getInX() + this.region.getInSide()) : this.region.getInX();
    }
    private int getY1() {
        int y = getPoint(this.region.getInY(), this.region.getInSide(), this.region.getOffY(), this.offY);
        return this.region.getInY() < y ? Math.min(y, this.region.getInY() + this.region.getInSide()) : this.region.getInY();
    }
    private int getX2() {
        int x = getPoint(this.region.getInX(), this.region.getInSide(), this.region.getOffX(), this.offX) + side;
        return this.region.getInX() < x ? Math.min(x, this.region.getInX() + this.region.getInSide()) : this.region.getInX();
    }
    private int getY2() {
        int y = getPoint(this.region.getInY(), this.region.getInSide(), this.region.getOffY(), this.offY) + side;
        return this.region.getInY() < y ? Math.min(y, this.region.getInY() + this.region.getInSide()) : this.region.getInY();
    }

    public void drawSelection(MatrixStack matrices) {
        matrices.push();

        fill(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), Color.GREEN.getRGB());

        matrices.pop();
    }
}
