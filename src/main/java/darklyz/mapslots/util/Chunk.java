package darklyz.mapslots.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.Optional;

public class Chunk extends DrawableHelper {
    private static final int side = 30;
    private final Region region;
    public final Integer mapId;
    private final int offX, offY;

    public Chunk(Region region, PacketByteBuf buf) {
        this(region, buf.readOptional(PacketByteBuf::readInt).orElse(null), buf.readInt(), buf.readInt());
    }
    public Chunk(Region region, int mouseX, int mouseY) {
        this(region, region.getMapId(),
                getOffset(region.getInX(), region.getInSide(), region.getOffX(), mouseX),
                getOffset(region.getInY(), region.getInSide(), region.getOffY(), mouseY)
        );
    }
    public Chunk(Region region, Integer mapId, int offX, int offY) {
        this.region = region;
        this.mapId = mapId;
        this.offX = offX;
        this.offY = offY;
    }

    public boolean equals(Object obj) {
        return obj instanceof Chunk chunk && (Objects.equals(this.mapId, chunk.mapId) || (this.offX == chunk.offX && this.offY == chunk.offY));
    }

    public PacketByteBuf toBuffer() {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeOptional(Optional.ofNullable(this.mapId), PacketByteBuf::writeInt);
        buf.writeInt(this.offX);
        buf.writeInt(this.offY);

        return buf;
    }

    private static int getCenter(int lim, int limSide, int cOff) { return lim + limSide/2 + cOff; }
    private static int getPoint(int lim, int limSide, int cOff, int off) {
        return getCenter(lim, limSide, cOff) + off * side;
    }
    private static int getOffset(int lim, int limSide, int cOff, int mouse) {
        int center = getCenter(lim, limSide, cOff);
        return (mouse - center) / side - (mouse < center ? 1 : 0);
    }

    private int getTrueX() {
        return getPoint(this.region.getInX(), this.region.getInSide(), this.region.getOffX(),  this.offX);
    }
    private int getTrueY() {
        return getPoint(this.region.getInY(), this.region.getInSide(), this.region.getOffY(), this.offY);
    }

    private int getX1() {
        int x = this.getTrueX();
        return this.region.getInX() < x ? Math.min(x, this.region.getInX() + this.region.getInSide()) : this.region.getInX();
    }
    private int getY1() {
        int y = this.getTrueY();
        return this.region.getInY() < y ? Math.min(y, this.region.getInY() + this.region.getInSide()) : this.region.getInY();
    }
    private int getX2() {
        int x = this.getTrueX() + side;
        return this.region.getInX() < x ? Math.min(x, this.region.getInX() + this.region.getInSide()) : this.region.getInX();
    }
    private int getY2() {
        int y = this.getTrueY() + side;
        return this.region.getInY() < y ? Math.min(y, this.region.getInY() + this.region.getInSide()) : this.region.getInY();
    }

    public void drawMap(MatrixStack matrices, MinecraftClient client, int alpha) {
        matrices.push();

        try (MapTexture mapTexture = new MapTexture(client, this.mapId)) {
            mapTexture.draw(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getTrueX(), this.getTrueY(), alpha);
        }

        matrices.pop();
    }

    private static class MapTexture implements AutoCloseable {
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
            if (this.texture.getImage() != null && this.state != null)
                for (int y = 0; y < 128; y++) for (int x = 0; x < 128; x++)
                    this.texture.getImage().setColor(x, y, MapColor.getRenderColor(this.state.colors[x + y * 128]));
            this.texture.upload();
        }

        private void draw(MatrixStack matrices, int x1, int y1, int x2, int y2, int trueX, int trueY, int alpha) {
            this.updateTexture();

            float u1 = (float)(x1 - trueX) / side;
            float v1 = (float)(y1 - trueY) / side;
            float u2 = (float)(x2 - trueX) / side;
            float v2 = (float)(y2 - trueY) / side;

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            VertexConsumerProvider.Immediate immediate =
                    VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            VertexConsumer vertexConsumer = immediate.getBuffer(this.renderLayer);

            vertexConsumer.vertex(matrix4f, x1, y2, 0.0f).color(255, 255, 255, alpha).texture(u1, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x2, y2, 0.0f).color(255, 255, 255, alpha).texture(u2, v2).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x2, y1, 0.0f).color(255, 255, 255, alpha).texture(u2, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();
            vertexConsumer.vertex(matrix4f, x1, y1, 0.0f).color(255, 255, 255, alpha).texture(u1, v1).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).next();

            immediate.draw();
        }

        public void close() { this.texture.close(); }
    }
}
