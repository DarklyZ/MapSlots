package darklyz.mapslots.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import darklyz.mapslots.packet.ChunksPacket;
import darklyz.mapslots.util.Chunk;
import darklyz.mapslots.util.Region;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class MapSlotsWidget extends DrawableHelper implements Drawable, Region {
    private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
    private static final int side = 166;
    public final ArrayList<Chunk> chunks = new ArrayList<>();
    private final MinecraftClient client = MinecraftClient.getInstance();
    public final Inventory inventory = new SimpleInventory(2);
    private boolean open = false;
    private int mX, mY, cOffX, cOffY, _cOffX, _cOffY;
    private boolean mouseDown = false;
    private int chunkSide = 30;
    private int parentX, parentY;

    public boolean isOpen() { return this.open; }
    public void setOpen(boolean opened) { this.open = opened; }
    public void toggleOpen() { this.setOpen(!this.isOpen()); }

    public boolean isInsertMode() {
        return this.inventory.getStack(0).isOf(Items.FILLED_MAP);
    }
    public boolean isRemoveMode() {
        return this.inventory.getStack(0).isEmpty();
    }

    public void initialize(int parentX, int parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }
    
    public int getMoveX(int parentX) {
        this.parentX = parentX + (this.isOpen() ? side / 2 : -side / 2);
        return this.parentX;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0.0f, 0.0f, 100.0f);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexture(matrices, this.getOutX(), this.getOutY(), side, side, 0, 0, 64, 64, 64, 64);

        for (Chunk chunk : this.chunks)
            chunk.drawMap(matrices, this.client, 255);

        if (this.isMouseInsideBounds(mouseX, mouseY) && this.isInsertMode())
            new Chunk(this, mouseX, mouseY).drawMap(matrices, this.client, 120);

        matrices.pop();
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY) {
        return !this.isOpen() || mouseX < this.getOutX()-18 || mouseY < this.getOutY() || mouseX > this.getOutX() + side || mouseY > this.getOutY() + side;
    }
    private boolean isMouseInsideBounds(double mouseX, double mouseY) {
        return this.isOpen() && mouseX >= this.getInX() && mouseY >= this.getInY() && mouseX <= this.getInX() + this.getInSide() && mouseY <= this.getInY() + this.getInSide();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseInsideBounds(mouseX, mouseY) && this.isRemoveMode() && InputUtil.fromTranslationKey("key.mouse.left").getCode() == button) {
            this.mX = (int)mouseX;
            this.mY = (int)mouseY;
            this._cOffX = this.cOffX;
            this._cOffY = this.cOffY;
            this.mouseDown = true;
        }
        return true;
    }
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mX = this.mY = this._cOffX = this._cOffY = 0;
        this.mouseDown = false;

        if (this.isMouseInsideBounds(mouseX, mouseY))
            ChunksPacket.sendC2S(new Chunk(this, (int)mouseX, (int)mouseY), button);

        return true;
    }
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.mouseDown) {
            this.cOffX = this._cOffX + (int)mouseX - this.mX;
            this.cOffY = this._cOffY + (int)mouseY - this.mY;
        }
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int futureSide = this.chunkSide + 20 * (int)amount;

        if (this.isMouseInsideBounds(mouseX, mouseY) && futureSide >= 30 && futureSide <= 330) {
            int x = (int)mouseX - this.getCenterX();
            int y = (int)mouseY - this.getCenterY();

            int cX = x / this.chunkSide + (x >= 0 ? 1 : -1);
            int cY = y / this.chunkSide + (y >= 0 ? 1 : -1);

            this.cOffX -= x * cX * futureSide / (cX * this.chunkSide) - x;
            this.cOffY -= y * cY * futureSide / (cY * this.chunkSide) - y;

            this.chunkSide = futureSide;
        }
        return true;
    }

    public Integer getMapId() {
        return FilledMapItem.getMapId(this.inventory.getStack(0));
    }
    public int getInX() { return this.getOutX() + 5; }
    public int getInY() { return this.getOutY() + 5; }
    public int getInSide() { return this.getOutSide() - 10; }
    public int getOutX() { return this.parentX-2 - side; }
    public int getOutY() { return this.parentY; }
    public int getOutSide() { return side; }
    public int getCenterX() {
        return this.getInX() + this.getInSide()/2 + this.cOffX;
    }
    public int getCenterY() {
        return this.getInY() + this.getInSide()/2 + this.cOffY;
    }
    public int getChunkSide() { return this.chunkSide; }
}
