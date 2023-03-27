package darklyz.mapslots.utils;

import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Objects;

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

    public void drawMap(MatrixStack matrices, MinecraftClient client, Integer mapId) {
        matrices.push();

        new MapTexture(client, mapId).draw(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2());

        matrices.pop();
    }

    private static class MapTexture {
        private final MapState state;
        private final NativeImageBackedTexture texture;
        private final RenderLayer renderLayer;

        private MapTexture(MinecraftClient client, Integer mapId) {
            this.state = FilledMapItem.getMapState(mapId, client.world);
            this.texture = new NativeImageBackedTexture(128, 128, true);
            Identifier identifier = client.getTextureManager().registerDynamicTexture("mapslots/" + mapId, this.texture);
            this.renderLayer = RenderLayer.getText(identifier);
        }

        private void updateTexture() {
            NativeImage image = Objects.requireNonNull(this.texture.getImage());
            for (int y = 0; y < 128; y++)
                for (int x = 0; x < 128; x++)
                    image.setColor(x, y, MapColor.getRenderColor(this.state.colors[x + y * 128]));
            this.texture.upload();
        }

        private void draw(MatrixStack matrices, int x1, int y1, int x2, int y2) {
            this.updateTexture();

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            VertexConsumerProvider.Immediate immediate =
                    VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            VertexConsumer vertexConsumer = immediate.getBuffer(this.renderLayer);

            vertexConsumer.vertex(matrix4f, x1, y2, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x2, y2, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x2, y1, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x1, y1, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();

            immediate.draw();
        }
    }
}
