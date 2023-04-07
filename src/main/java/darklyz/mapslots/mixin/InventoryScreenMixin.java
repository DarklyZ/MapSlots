package darklyz.mapslots.mixin;

import darklyz.mapslots.MapSlots;
import darklyz.mapslots.drawable.MapSlotsWidget;
import darklyz.mapslots.util.MapSlotsHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
	private static final Identifier MAP_BUTTON_TEXTURE = new Identifier(
			MapSlots.LOGGER.getName(), "textures/gui/map_button.png");
	private final MapSlotsWidget mapSlotsWidget =
			((MapSlotsHandler)this.getScreenHandler()).getMSWidget();
	private TexturedButtonWidget bookButton, mapButton;
	@Shadow @Final private static Identifier RECIPE_BUTTON_TEXTURE;
	@Shadow @Final private RecipeBookWidget recipeBook;
	@Shadow private boolean mouseDown;

	private InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
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

	private void updatePositionButtons() {
		this.bookButton.setPosition(this.x + 104, this.height / 2 - 22);
		this.mapButton.setPosition(this.x + (this.bookButton.active ? 126 : 104), this.height / 2 - 22);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private void init(CallbackInfo ci) {
		this.mapSlotsWidget.initialize(this.client, this.x, this.y);

		this.initButtons();

		this.addDrawable(this.mapSlotsWidget);

		if (this.recipeBook.isOpen()) {
			this.mapButton.active =! this.mapButton.active;
			this.mapSlotsWidget.setOpen(false);
		}
		if (this.mapSlotsWidget.isOpen()) {
			this.x = this.mapSlotsWidget.getMoveX(this.x);

			this.bookButton.active =! this.bookButton.active;
			this.updatePositionButtons();
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private Element addDrawableChild(InventoryScreen instance, Element element) {
		this.addDrawableChild(this.bookButton);
		this.addDrawableChild(this.mapButton);
		return this.bookButton;
	}

	@Inject(at = @At("TAIL"), method = "isClickOutsideBounds", cancellable = true)
	private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.mapSlotsWidget.isClickOutsideBounds(mouseX, mouseY) && cir.getReturnValue());
	}

	@Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
	private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (this.mapSlotsWidget.mouseClicked(mouseX, mouseY, button))
			cir.setReturnValue(true);
	}
	@Inject(at = @At("HEAD"), method = "mouseReleased", cancellable = true)
	private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (this.mapSlotsWidget.mouseReleased(mouseX, mouseY, button))
			cir.setReturnValue(true);
	}
	public void mouseMoved(double mouseX, double mouseY) {
		this.mapSlotsWidget.mouseMoved(mouseX, mouseY);
	}
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (this.mapSlotsWidget.mouseScrolled(mouseX, mouseY, amount))
			return true;
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}
