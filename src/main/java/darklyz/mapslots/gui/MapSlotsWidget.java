package darklyz.mapslots.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import darklyz.mapslots.module.Chunk;
import darklyz.mapslots.module.ChunkSync;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.stream.Stream;

public class MapSlotsWidget extends DrawableHelper implements Drawable, Element, Selectable, Chunk.RegionGetter {
	public interface Giving {
		MapSlotsWidget getMSWidget();
	}
	public interface Coordinating {
		int getMSWidgetX();
		int getMSWidgetY();
		int getMSWidgetSide();
	}

	private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
	private final MinecraftClient client = MinecraftClient.getInstance();
	public final ArrayList<Chunk> chunks = Lists.newArrayList();
	public final Inventory inventory = new SimpleInventory(2);
	private int mOffX, mOffY, cOffX, cOffY, chunkSide = 30;
	private boolean open = false, focused = false;

	public boolean isOpen() { return this.open; }
	public void setOpen(boolean opened) { this.open = opened; }
	public void toggleOpen() { this.setOpen(!this.isOpen()); }

	public boolean isInsertMode() { return this.inventory.getStack(0).isOf(Items.FILLED_MAP); }
	public boolean isRemoveMode() { return this.inventory.getStack(0).isEmpty(); }

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!this.isOpen()) return;

		matrices.push();
		matrices.translate(0.0f, 0.0f, 100.0f);

		RenderSystem.setShaderTexture(0, TEXTURE);

		drawTexture(matrices, this.outX(), this.outY(), this.side(), this.side(), 0, 0, 64, 64, 64, 64);

		if (this.isMouseOver(mouseX, mouseY) && this.isInsertMode())
			new Chunk(this, mouseX, mouseY).drawMap(matrices, this.client, 120);

		this.chunks.forEach(chunk -> chunk.drawMap(matrices, this.client, 255));

		matrices.pop();
	}

	public boolean isClickOutsideBounds(double mouseX, double mouseY) {
		return !this.isOpen() || mouseX < this.outX() || mouseY < this.outY() || mouseX > this.outX() + this.side() || mouseY > this.outY() + this.side();
	}
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.isOpen() && mouseX >= this.inX() && mouseY >= this.inY() && mouseX <= this.inX() + this.inSide() && mouseY <= this.inY() + this.inSide();
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.isMouseOver(mouseX, mouseY) || !this.isRemoveMode() || InputUtil.fromTranslationKey("key.mouse.left").getCode() != button)
			return false;

		this.mOffX = this.cOffX - (int)mouseX;
		this.mOffY = this.cOffY - (int)mouseY;
		return true;
	}
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (Stream.of("key.mouse.left", "key.mouse.right").allMatch(key -> InputUtil.fromTranslationKey(key).getCode() != button))
			return false;

		ChunkSync.sendC2S(new Chunk(this, (int)mouseX, (int)mouseY), button);
		return true;
	}
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		this.cOffX = this.mOffX + (int)mouseX;
		this.cOffY = this.mOffY + (int)mouseY;
		return true;
	}
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		int futureSide = this.chunkSide + 20 * (int)amount;

		if (this.isFocused() || futureSide < 30 || futureSide > 330) return false;

		int x = (int)mouseX - this.centerX();
		int y = (int)mouseY - this.centerY();

		int cX = x / this.chunkSide + (x >= 0 ? 1 : -1);
		int cY = y / this.chunkSide + (y >= 0 ? 1 : -1);

		this.cOffX -= x * cX * futureSide / (cX * this.chunkSide) - x;
		this.cOffY -= y * cY * futureSide / (cY * this.chunkSide) - y;

		this.chunkSide = futureSide;
		return true;
	}

	public void setFocused(boolean focused) {
		if (!focused) this.mOffX = this.mOffY = 0;

		this.focused = focused;
	}
	public boolean isFocused() { return this.focused; }

	public Integer mapId() { return FilledMapItem.getMapId(this.inventory.getStack(0)); }
	public int inX() { return this.outX() + this.side()/32; }
	public int inY() { return this.outY() + this.side()/32; }
	public int inSide() { return this.side() - this.side()/16; }
	public int outX() { return (this.client.currentScreen instanceof Coordinating screen) ? screen.getMSWidgetX() : 0; }
	public int outY() { return (this.client.currentScreen instanceof Coordinating screen) ? screen.getMSWidgetY() : 0; }
	public int side() { return (this.client.currentScreen instanceof Coordinating screen) ? screen.getMSWidgetSide() : 0; }
	public int centerX() { return this.inX() + this.inSide()/2 + this.cOffX; }
	public int centerY() { return this.inY() + this.inSide()/2 + this.cOffY; }
	public int chunkSide() { return this.chunkSide; }

	public SelectionType getType() { return this.isOpen() ? SelectionType.HOVERED : SelectionType.NONE; }
	public boolean isNarratable() { return false; }
	public void appendNarrations(NarrationMessageBuilder builder) {}
}
