package darklyz.mapslots.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
	private HandledScreenMixin(Text title) { super(title); }

	@Inject(at = @At("HEAD"), method = "mouseReleased", cancellable = true)
	private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if (super.mouseReleased(mouseX, mouseY, button))
			cir.setReturnValue(true);
	}

	@Inject(at = @At("HEAD"), method = "mouseDragged", cancellable = true)
	private void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
			cir.setReturnValue(true);
	}
}
