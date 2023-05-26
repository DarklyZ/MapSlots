package darklyz.mapslots.mixin;

import darklyz.mapslots.MapSlots;
import darklyz.mapslots.abc.MapSlotsScreen;
import darklyz.mapslots.drawable.MapSlotsWidget;
import darklyz.mapslots.abc.MapSlotsHandler;
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

	private void updateActiveButtons() {
		this.mapButton.active = !this.recipeBook.isOpen();
		this.bookButton.active = !this.mapSlotsWidget.isOpen();
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private void init(CallbackInfo ci) {
		this.mapSlotsWidget.initialize(this.x, this.y);

		if (this.recipeBook.isOpen())
			this.mapSlotsWidget.setOpen(false);

		if (this.mapSlotsWidget.isOpen())
			this.x = this.mapSlotsWidget.getMoveX(this.x);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;", ordinal = 0), method = "init")
	private Element addDrawableChild(InventoryScreen instance, Element element) {
		this.bookButton = this.addDrawableChild(new TexturedButtonWidget(0, 0, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			this.mouseDown = true;
			this.updateActiveButtons();
		}) {
			public int getX() { return InventoryScreenMixin.this.x + 104; }
			public int getY() { return InventoryScreenMixin.this.height / 2 - 22; }
		});
		this.mapButton = this.addDrawableChild(new TexturedButtonWidget(0, 0, 20, 18, 0, 0, 19, MAP_BUTTON_TEXTURE, (button) -> {
			this.mapSlotsWidget.toggleOpen();
			this.x = this.mapSlotsWidget.getMoveX(this.x);
			this.updateActiveButtons();
		}) {
			public int getX() { return InventoryScreenMixin.this.x + (InventoryScreenMixin.this.bookButton.active ? 126 : 104); }
			public int getY() { return InventoryScreenMixin.this.height / 2 - 22; }
		});
		this.updateActiveButtons();

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
