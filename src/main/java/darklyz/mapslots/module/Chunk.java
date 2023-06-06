package darklyz.mapslots.module;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.Optional;

public class Chunk {
	public interface RegionGetter {
		boolean isOpen();
		Integer mapId();
		int inX();
		int inY();
		int inSide();
		int outX();
		int outY();
		int side();
		int centerX();
		int centerY();
		int chunkSide();
	}
	public interface UpdateManager {
		boolean isNeedsUpdate();
		void setNeedsUpdate(boolean needsUpdate);
	}

	private final RegionGetter region;
	public final Integer mapId;
	private final int offX, offY;
	private MapTexture mapTexture;

	public Chunk(RegionGetter region, Integer mapId, int offX, int offY) {
		this.region = region;
		this.mapId = mapId;
		this.offX = offX;
		this.offY = offY;
	}
	public Chunk(RegionGetter region, PacketByteBuf buf) {
		this(region, buf.readOptional(PacketByteBuf::readInt).orElse(null), buf.readInt(), buf.readInt());
	}
	public Chunk(RegionGetter region, int mouseX, int mouseY) {
		this(region, region.mapId(), getOffset(region.centerX(), region.chunkSide(), mouseX), getOffset(region.centerY(), region.chunkSide(), mouseY));
	}
	private static int getOffset(int center, int side, int mouse) {
		return (mouse - center) / side - (mouse < center ? 1 : 0);
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

	private int getTrueX() { return this.region.centerX() + this.offX * this.region.chunkSide(); }
	private int getTrueY() { return this.region.centerY() + this.offY * this.region.chunkSide(); }

	private int getX1() {
		int x = this.getTrueX();
		return this.region.inX() < x ? Math.min(x, this.region.inX() + this.region.inSide()) : this.region.inX();
	}
	private int getY1() {
		int y = this.getTrueY();
		return this.region.inY() < y ? Math.min(y, this.region.inY() + this.region.inSide()) : this.region.inY();
	}
	private int getX2() {
		int x = this.getTrueX() + this.region.chunkSide();
		return this.region.inX() < x ? Math.min(x, this.region.inX() + this.region.inSide()) : this.region.inX();
	}
	private int getY2() {
		int y = this.getTrueY() + this.region.chunkSide();
		return this.region.inY() < y ? Math.min(y, this.region.inY() + this.region.inSide()) : this.region.inY();
	}

	public void drawMap(MatrixStack matrices, MinecraftClient client, int alpha) {
		MapState state = FilledMapItem.getMapState(this.mapId, client.world);
		if (state == null) return;

		if (this.mapTexture == null)
			this.mapTexture = new MapTexture(client.getTextureManager(), state, this.mapId);

		this.mapTexture.draw(matrices, this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getTrueX(), this.getTrueY(), this.region.chunkSide(), alpha);
	}

	private static class MapTexture implements AutoCloseable {
		@NotNull private final MapState state;
		private final NativeImageBackedTexture texture;
		private final RenderLayer renderLayer;

		private MapTexture(TextureManager textureManager, @NotNull MapState state, Integer mapId) {
			((UpdateManager)state).setNeedsUpdate(true);

			this.state = state;
			this.texture = new NativeImageBackedTexture(128, 128, true);
			Identifier identifier = textureManager.registerDynamicTexture("mapslots/" + mapId, this.texture);
			this.renderLayer = RenderLayer.getText(identifier);
		}

		private void updateTexture() {
			NativeImage image = Objects.requireNonNull(this.texture.getImage());
			for (int y = 0; y < 128; y++) for (int x = 0; x < 128; x++)
				image.setColor(x, y, MapColor.getRenderColor(this.state.colors[x + y * 128]));
			this.texture.upload();
		}

		private void draw(MatrixStack matrices, int x1, int y1, int x2, int y2, int trueX, int trueY, int side, int alpha) {
			UpdateManager update = (UpdateManager)this.state;
			if (update.isNeedsUpdate()) {
				this.updateTexture();
				update.setNeedsUpdate(false);
			}

			float u1 = (float)(x1 - trueX) / side;
			float v1 = (float)(y1 - trueY) / side;
			float u2 = (float)(x2 - trueX) / side;
			float v2 = (float)(y2 - trueY) / side;

			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
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
