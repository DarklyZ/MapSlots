package darklyz.mapslots.mixin;

import darklyz.mapslots.MapSlots;
import darklyz.mapslots.abc.MapSlotsHandler;
import darklyz.mapslots.abc.MapSlotsScreen;
import darklyz.mapslots.drawable.MapSlotsWidget;
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
abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements MapSlotsScreen {
	private static final Identifier MAP_BUTTON_TEXTURE = new Identifier(MapSlots.LOGGER.getName(), "textures/gui/map_button.png");
	private final MapSlotsWidget mapSlotsWidget = ((MapSlotsHandler)this.getScreenHandler()).getMSWidget();
	private TexturedButtonWidget bookButton, mapButton;
	@Shadow @Final private static Identifier RECIPE_BUTTON_TEXTURE;
	@Shadow @Final private RecipeBookWidget recipeBook;
	@Shadow private boolean mouseDown;

	private InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	public void clearAndInit() { super.clearAndInit(); }
	public int getMSWidgetX() { return this.x-2 - this.getMSWidgetSide(); }
	public int getMSWidgetY() { return (this.height - this.getMSWidgetSide()) / 2; }
	public int getMSWidgetSide() { return this.backgroundHeight; }

	private void updateButtons() {
		this.mapButton.active = !this.recipeBook.isOpen();
		this.bookButton.active = !this.mapSlotsWidget.isOpen();
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private void init(CallbackInfo ci) {
		if (this.recipeBook.isOpen()) this.mapSlotsWidget.setOpen(false);

		if (this.mapSlotsWidget.isOpen()) this.x += this.getMSWidgetSide() / 2;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private Element addDrawableChild(InventoryScreen instance, Element element) {
		this.bookButton = this.addDrawableChild(new TexturedButtonWidget(0, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			this.mouseDown = true;
			this.updateButtons();
		}) {
			public int getX() { return InventoryScreenMixin.this.x + 104; }
		});
		this.mapButton = this.addDrawableChild(new TexturedButtonWidget(0, this.height / 2 - 22, 20, 18, 0, 0, 19, MAP_BUTTON_TEXTURE, (button) -> {
			this.mapSlotsWidget.toggleOpen();
			this.x += this.getMSWidgetSide() / (this.mapSlotsWidget.isOpen() ? 2 : -2);
			this.updateButtons();
		}) {
			public int getX() { return InventoryScreenMixin.this.x + (bookButton.active ? 126 : 104); }
		});
		this.updateButtons();

		this.addDrawableChild(this.mapSlotsWidget);
		this.mapSlotsWidget.chunks.forEach(this::addDrawable);
		return this.bookButton;
	}

	@Inject(at = @At("TAIL"), method = "isClickOutsideBounds", cancellable = true)
	private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.mapSlotsWidget.isClickOutsideBounds(mouseX, mouseY) && cir.getReturnValue());
	}

	@Inject(at = @At("HEAD"), method = "mouseReleased")
	private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (this.getFocused() instanceof MapSlotsWidget) this.setFocused(null);
	}
}
