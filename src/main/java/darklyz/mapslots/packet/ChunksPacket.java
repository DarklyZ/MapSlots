package darklyz.mapslots.packet;

import darklyz.mapslots.MapSlots;
import darklyz.mapslots.abc.MapSlotsScreen;
import darklyz.mapslots.drawable.MapSlotsWidget;
import darklyz.mapslots.drawable.Chunk;
import darklyz.mapslots.abc.MapSlotsHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ChunksPacket {
    public static final Identifier ID = new Identifier(MapSlots.LOGGER.getName(), "chunks");

    public static void sendC2S(Chunk chunk, int button) {
        PacketByteBuf buf = chunk.toBuffer();
        buf.writeInt(button);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void sendS2C(ServerPlayerEntity player, Chunk chunk) {
        PacketByteBuf buf = chunk.toBuffer();
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void handleC2S(MinecraftServer server,
                                 ServerPlayerEntity player,
                                 ServerPlayNetworkHandler ignoredNetwork,
                                 PacketByteBuf buf,
                                 PacketSender ignoredSender) {
        MapSlotsWidget mSWidget = ((MapSlotsHandler)player.playerScreenHandler).getMSWidget();
        Chunk chunk = new Chunk(mSWidget, buf);
        int button = buf.readInt();

        server.execute(() -> {
            int index = mSWidget.chunks.indexOf(chunk);

            if (mSWidget.isInsertMode() && index < 0 && InputUtil.fromTranslationKey("key.mouse.left").getCode() == button) {
                mSWidget.inventory.removeStack(0);
                mSWidget.chunks.add(chunk);
            } else if (mSWidget.isRemoveMode() && index >= 0 && InputUtil.fromTranslationKey("key.mouse.right").getCode() == button) {
                ItemStack stack = new ItemStack(Items.FILLED_MAP);
                NbtCompound nbt = new NbtCompound();

                nbt.putInt("map", mSWidget.chunks.get(index).mapId);
                stack.setNbt(nbt);

                mSWidget.chunks.remove(index);
                mSWidget.inventory.setStack(0, stack);
            } else return;

            sendS2C(player, chunk);
        });
    }

    public static void handleS2C(MinecraftClient client,
                                 ClientPlayNetworkHandler ignoredNetwork,
                                 PacketByteBuf buf,
                                 PacketSender ignoredSender) {
        if (client.player == null)
            return;

        MapSlotsWidget mSWidget = ((MapSlotsHandler)client.player.playerScreenHandler).getMSWidget();
        Chunk chunk = new Chunk(mSWidget, buf);

        client.execute(() -> {
            if (chunk.mapId == null)
                mSWidget.chunks.remove(chunk);
            else mSWidget.chunks.add(chunk);

            if (client.currentScreen instanceof MapSlotsScreen screen)
                screen.clearAndInit();
        });
    }
}