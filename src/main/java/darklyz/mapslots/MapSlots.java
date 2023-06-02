package darklyz.mapslots;

import darklyz.mapslots.module.ChunksPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSlots implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("mapslots");

	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(ChunksPacket.ID, ChunksPacket::handleC2S);
		ClientPlayNetworking.registerGlobalReceiver(ChunksPacket.ID, ChunksPacket::handleS2C);
	}
}
