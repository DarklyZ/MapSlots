package darklyz.mapslots.drawables;

import com.mojang.blaze3d.systems.RenderSystem;
import darklyz.mapslots.utils.Chunk;
import darklyz.mapslots.utils.Region;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class MapSlotsWidget extends DrawableHelper implements Drawable, Region {
    private static final Identifier TEXTURE = new Identifier("textures/map/map_background.png");
    private static final int side = 166;
    public final Inventory inventory = new SimpleInventory(3);
    private final HashMap<Integer, Chunk> maps = new HashMap<>();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private boolean open = false;
    private int parentX;
    private int parentY;

    public MapSlotsWidget() {
        this.maps.put(6, Chunk.ofOffset(this, 0, 0));
    }

    public boolean isOpen() { return this.open; }
    public void setOpen(boolean opened) { this.open = opened; }
    public void toggleOpen() { this.setOpen(!isOpen()); }

    public boolean isChangeMode() {
        return !this.inventory.getStack(0).isEmpty();
    }

    public Chunk getChunk(int mouseX, int mouseY) {
        return Chunk.ofMouse(this, mouseX, mouseY);
    }

    public void initialize(int parentX, int parentY) {
        this.parentX = parentX;
        this.parentY = parentY;
    }
    
    public int getMoveX(int parentX) {
        this.parentX = parentX + (isOpen() ? side / 2 : -side / 2);
        return this.parentX;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0.0f, 0.0f, 100.0f);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexture(matrices, this.getOutX(), this.getOutY(), side, side, 0, 0, 64, 64, 64, 64);

        for (Integer key : this.maps.keySet())
            this.maps.get(key).drawMap(matrices, this.client, key);

        if (this.isChangeMode() && this.isMouseInsideBounds(mouseX, mouseY))
            this.getChunk(mouseX, mouseY).drawSelection(matrices);

        matrices.pop();
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY) {
        return !isOpen() || mouseX < this.getOutX()-18 || mouseY < this.getOutY() || mouseX > this.getOutX() + side || mouseY > this.getOutY() + side;
    }

    public boolean isMouseInsideBounds(double mouseX, double mouseY) {
        return mouseX >= this.getInX() && mouseY >= this.getInY() && mouseX <= this.getInX() + this.getInSide() && mouseY <= this.getInY() + this.getInSide();
    }

    public int getInX() { return this.getOutX() + 5; }
    public int getInY() { return this.getOutY() + 5; }
    public int getInSide() { return this.getOutSide() - 10; }
    public int getOutX() { return this.parentX-2 - side; }
    public int getOutY() { return this.parentY; }
    public int getOutSide() { return side; }
    public int getOffX() { return 0; }
    public int getOffY() { return 0; }
}
