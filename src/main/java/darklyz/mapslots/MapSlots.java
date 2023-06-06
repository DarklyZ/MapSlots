package darklyz.mapslots;

import darklyz.mapslots.module.ChunkSync;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSlots implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mapslots");

	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(ChunkSync.ID, ChunkSync::handleC2S);
		ClientPlayNetworking.registerGlobalReceiver(ChunkSync.ID, ChunkSync::handleS2C);
	}
}
