package darklyz.mapslots.mixin;

import darklyz.mapslots.module.Chunk;
import net.minecraft.item.map.MapState;
import net.minecraft.world.PersistentState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapState.class)
abstract class MapStateMixin extends PersistentState implements Chunk.UpdateManager {
	private boolean needsUpdate;

	public void markDirty() {
		super.markDirty();
		this.setNeedsUpdate(true);
	}

	public boolean isNeedsUpdate() { return this.needsUpdate; }
	public void setNeedsUpdate(boolean needsUpdate) { this.needsUpdate = needsUpdate; }
}