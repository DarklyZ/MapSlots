package darklyz.mapslots.mixin;

import darklyz.mapslots.ExampleMod;
import darklyz.mapslots.drawables.MapSlotsWidget;
import darklyz.mapslots.utils.MapSlotsHandler;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	private static final Identifier MAP_BUTTON_TEXTURE = new Identifier(
			ExampleMod.LOGGER.getName(), "textures/gui/map_button.png");
	private final MapSlotsWidget mapSlotsWidget =
			((MapSlotsHandler)this.getScreenHandler()).getMSWidget();
	private TexturedButtonWidget bookButton;
	private TexturedButtonWidget mapButton;
	@Shadow @Final private static Identifier RECIPE_BUTTON_TEXTURE;
	@Shadow @Final private RecipeBookWidget recipeBook;
	@Shadow private boolean mouseDown;

	public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	private void initButtons() {
		this.bookButton = new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			this.mouseDown = true;

			this.mapButton.active =! this.mapButton.active;
			this.updatePositionButtons();
		});
		this.mapButton = new TexturedButtonWidget(this.x + 126, this.height / 2 - 22, 20, 18, 0, 0, 19, MAP_BUTTON_TEXTURE, (button) -> {
			this.mapSlotsWidget.toggleOpen();
			this.x = this.mapSlotsWidget.getMoveX(this.x);

			this.bookButton.active =! this.bookButton.active;
			this.updatePositionButtons();
		});
	}

	@Inject(at = @At(value = "TAIL"), method = "init")
	private void init(CallbackInfo ci) {
		this.mapSlotsWidget.initialize(this.x, this.y);

		this.clearChildren();
		this.initButtons();

		this.addDrawableChild(this.bookButton);
		this.addDrawableChild(this.mapButton);

		if (this.recipeBook.isOpen())
			this.mapButton.active =! this.mapButton.active;

		if (this.mapSlotsWidget.isOpen()) {
			this.x = this.mapSlotsWidget.getMoveX(this.x);

			this.bookButton.active =! this.bookButton.active;
			this.updatePositionButtons();
		}
	}

	@Inject(at = @At("JUMP"), method = "render")
	private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.mapSlotsWidget.isOpen())
			this.mapSlotsWidget.render(matrices, mouseX, mouseY, delta);
	}

	private void updatePositionButtons() {
		this.bookButton.setPos(this.x + 104, this.height / 2 - 22);
		this.mapButton.setPos(this.x + (this.bookButton.active ? 126 : 104), this.height / 2 - 22);
	}

	@Inject(at = @At("TAIL"), method = "isClickOutsideBounds", cancellable = true)
	public void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.mapSlotsWidget.isClickOutsideBounds(mouseX, mouseY, left, top) && cir.getReturnValue());
	}
}
