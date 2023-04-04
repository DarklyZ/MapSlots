package darklyz.mapslots.packets;

import darklyz.mapslots.MapSlots;
import darklyz.mapslots.drawables.MapSlotsWidget;
import darklyz.mapslots.utils.Chunk;
import darklyz.mapslots.utils.MapSlotsHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
        PacketByteBuf buf = PacketByteBufs.create();

        chunk.writeBuf(buf);
        buf.writeInt(button);

        ClientPlayNetworking.send(ID, buf);
    }

    public static void sendS2C(ServerPlayerEntity player, Chunk chunk) {
        PacketByteBuf buf = PacketByteBufs.create();

        chunk.writeBuf(buf);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void handleC2S(MinecraftServer server,
                                 ServerPlayerEntity player,
                                 ServerPlayNetworkHandler ignoredNetwork,
                                 PacketByteBuf buf,
                                 PacketSender ignoredSender) {
        MapSlotsWidget mSWidget = ((MapSlotsHandler) player.playerScreenHandler).getMSWidget();
        Chunk chunk = Chunk.readBuf(mSWidget, buf);
        int button = buf.readInt();
        int index = mSWidget.chunks.indexOf(chunk);

        server.execute(() -> {
            if (mSWidget.isInsertMode() && !mSWidget.chunks.contains(chunk) && InputUtil.fromTranslationKey("key.mouse.left").getCode() == button) {
                mSWidget.inventory.removeStack(0);
                mSWidget.chunks.add(chunk);

                sendS2C(player, chunk);
            } else if (mSWidget.isRemoveMode() && index >= 0 && InputUtil.fromTranslationKey("key.mouse.right").getCode() == button) {
                ItemStack stack = new ItemStack(Items.FILLED_MAP);
                NbtCompound nbtCompound = new NbtCompound();

                nbtCompound.putInt("map", mSWidget.chunks.get(index).mapId);
                stack.setNbt(nbtCompound);

                mSWidget.chunks.remove(index);
                mSWidget.inventory.setStack(0, stack);

                sendS2C(player, chunk);
            }
        });
    }

    public static void handleS2C(MinecraftClient client,
                                 ClientPlayNetworkHandler ignoredNetwork,
                                 PacketByteBuf buf,
                                 PacketSender ignoredSender) {
        if (client.player == null)
            return;

        MapSlotsWidget mSWidget = ((MapSlotsHandler)client.player.playerScreenHandler).getMSWidget();
        Chunk chunk = Chunk.readBuf(mSWidget, buf);

        client.execute(() -> {
            if (chunk.mapId == null)
                mSWidget.chunks.remove(chunk);
            else mSWidget.chunks.add(chunk);
        });
    }
}